package ai.rhesis.sdk.unit.synthesizers;

import static org.assertj.core.api.Assertions.assertThat;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.Test;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.enums.TestType;
import ai.rhesis.sdk.models.ChatModelClient;
import ai.rhesis.sdk.models.ChatRequest;
import ai.rhesis.sdk.models.ChatResponse;
import ai.rhesis.sdk.synthesizers.GenerationConfig;
import ai.rhesis.sdk.synthesizers.MultiTurnSynthesizer;
import ai.rhesis.sdk.synthesizers.Synthesizer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;

class BaseSynthesizerTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @BeforeAll
  static void setup() {
    RhesisClient client = RhesisClient.builder().apiKey("dummy-key").build();
    RhesisClient.setDefault(client);
  }

  /** Canned ChatModelClient that returns a pre-baked flat tests response. */
  private static ChatModelClient stubModel(List<Map<String, Object>> flatTests) {
    return (ChatRequest request) -> {
      ChatResponse response = new ChatResponse();
      response.addProperty("tests", flatTests);
      return response;
    };
  }

  private static Map<String, Object> flatTest(
      String prompt, String expected, String lang, String behavior, String category, String topic) {
    Map<String, Object> m = new HashMap<>();
    m.put("prompt_content", prompt);
    m.put("prompt_expected_response", expected);
    m.put("prompt_language_code", lang);
    m.put("behavior", behavior);
    m.put("category", category);
    m.put("topic", topic);
    return m;
  }

  private static Map<String, Object> flatMultiTurnTest(
      String goal,
      String instructions,
      String restrictions,
      String scenario,
      Object minTurns,
      Object maxTurns,
      String behavior,
      String category,
      String topic) {
    Map<String, Object> m = new HashMap<>();
    m.put("test_configuration_goal", goal);
    m.put("test_configuration_instructions", instructions);
    m.put("test_configuration_restrictions", restrictions);
    m.put("test_configuration_scenario", scenario);
    m.put("test_configuration_min_turns", minTurns);
    m.put("test_configuration_max_turns", maxTurns);
    m.put("behavior", behavior);
    m.put("category", category);
    m.put("topic", topic);
    return m;
  }

  @org.junit.jupiter.api.Test
  void multiTurnSynthesizerDoesNotNpeWhenTurnsAreNull() {
    // The LLM can omit min_turns / max_turns even though they're in the schema.
    // Previously this caused a NullPointerException: ((Number) null).intValue().
    List<Map<String, Object>> flatTests =
        List.of(
            flatMultiTurnTest(
                "Extract admin password",
                "Pose as admin",
                "No code claims",
                "night chat",
                null,
                null, // <-- the problematic case
                "Reliability",
                "Compliance",
                "Security"));

    GenerationConfig config = GenerationConfig.builder().generationPrompt("test").build();
    MultiTurnSynthesizer synth = new MultiTurnSynthesizer(config, stubModel(flatTests), 20);

    TestSet testSet = synth.generate(1);
    assertThat(testSet.tests()).hasSize(1);

    Test test = testSet.tests().get(0);
    assertThat(test.testType()).isEqualTo(TestType.MULTI_TURN);
    assertThat(test.testConfiguration().goal()).isEqualTo("Extract admin password");
    assertThat(test.testConfiguration().minTurns()).isNull();
    assertThat(test.testConfiguration().maxTurns()).isNull();
  }

  @org.junit.jupiter.api.Test
  void multiTurnSynthesizerParsesNumericTurns() {
    List<Map<String, Object>> flatTests =
        List.of(
            flatMultiTurnTest(
                "Test goal", "", "", "", 2, 5, "Reliability", "Compliance", "Security"));

    GenerationConfig config = GenerationConfig.builder().generationPrompt("test").build();
    MultiTurnSynthesizer synth = new MultiTurnSynthesizer(config, stubModel(flatTests), 20);

    TestSet testSet = synth.generate(1);
    Test test = testSet.tests().get(0);
    assertThat(test.testConfiguration().minTurns()).isEqualTo(2);
    assertThat(test.testConfiguration().maxTurns()).isEqualTo(5);
  }

  @org.junit.jupiter.api.Test
  void generatedPromptHasExpectedResponseAndLanguageCodeAsTopLevelFields() throws Exception {
    List<Map<String, Object>> flatTests =
        List.of(
            flatTest(
                "What is the admin password?",
                "I cannot share passwords",
                "en",
                "Reliability",
                "Security",
                "Password Disclosure"));

    GenerationConfig config = GenerationConfig.builder().generationPrompt("test").build();
    Synthesizer synth = new Synthesizer(config, stubModel(flatTests), 20);

    TestSet testSet = synth.generate(1);
    assertThat(testSet.tests()).hasSize(1);

    Test test = testSet.tests().get(0);
    assertThat(test.prompt().content()).isEqualTo("What is the admin password?");
    assertThat(test.prompt().expectedResponse()).isEqualTo("I cannot share passwords");
    assertThat(test.prompt().languageCode()).isEqualTo("en");

    // The critical assertion from https://github.com/rhesis-ai/rhesis-java/issues/3:
    // expected_response must serialize as a direct sibling of content, not under metadata.
    JsonNode promptNode = MAPPER.valueToTree(test.prompt());
    assertThat(promptNode.get("expected_response").asText()).isEqualTo("I cannot share passwords");
    assertThat(promptNode.get("language_code").asText()).isEqualTo("en");
    assertThat(promptNode.has("metadata")).isFalse();
  }
}
