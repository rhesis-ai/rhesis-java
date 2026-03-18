package ai.rhesis.sdk.synthesizers;

import ai.rhesis.sdk.entities.Test;
import ai.rhesis.sdk.entities.TestConfiguration;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.enums.TestType;
import ai.rhesis.sdk.models.ChatModelClient;
import ai.rhesis.sdk.models.ChatRequest;
import ai.rhesis.sdk.models.ChatResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiTurnSynthesizer extends BaseSynthesizer {
  private final GenerationConfig config;

  public MultiTurnSynthesizer(GenerationConfig config, ChatModelClient modelClient, int batchSize) {
    super(modelClient, batchSize);
    this.config = config;
  }

  public MultiTurnSynthesizer(GenerationConfig config, int batchSize) {
    this(config, null, batchSize);
  }

  public MultiTurnSynthesizer(GenerationConfig config) {
    this(config, null, 20);
  }

  @Override
  public TestSet generate(int numTests) {
    List<Test> generatedTests = new ArrayList<>();
    int numBatches = numTests / batchSize;
    int currentBatchSize = batchSize;

    if (numBatches == 0) {
      numBatches = 1;
      currentBatchSize = numTests;
    }

    for (int i = 0; i < numBatches; i++) {
      generatedTests.addAll(generateBatch(currentBatchSize));
    }

    return new TestSet(
        null,
        "Synthesized TestSet (Multi-Turn)",
        "Generated with MultiTurnSynthesizer based on prompt: " + config.getGenerationPrompt(),
        TestType.MULTI_TURN,
        generatedTests);
  }

  private List<Test> generateBatch(int currentBatchSize) {
    Map<String, Object> context = new HashMap<>();
    context.put("generation_prompt", config.getGenerationPrompt());
    context.put("behaviors", config.getBehaviors());
    context.put("categories", config.getCategories());
    context.put("topics", config.getTopics());
    context.put("additional_context", config.getAdditionalContext());
    context.put("num_tests", currentBatchSize);

    String renderedPrompt = renderTemplate("multi_turn_synthesizer.jinja", context);

    Map<String, Object> schema = SchemaBuilder.buildMultiTurnSchema();
    ChatRequest request = new ChatRequest(renderedPrompt, 0.7, 4000, schema);

    ChatResponse response = modelClient.chat(request);

    return parseResponse(response);
  }

  @SuppressWarnings("unchecked")
  private List<Test> parseResponse(ChatResponse response) {
    List<Test> tests = new ArrayList<>();
    Map<String, Object> props = response.getProperties();

    if (!props.containsKey("tests")) {
      return tests;
    }

    List<Map<String, Object>> flatTests = (List<Map<String, Object>>) props.get("tests");

    for (Map<String, Object> flat : flatTests) {
      TestConfiguration testConfig =
          new TestConfiguration(
              (String) flat.get("test_configuration_goal"),
              (String) flat.get("test_configuration_instructions"),
              (String) flat.get("test_configuration_restrictions"),
              (String) flat.get("test_configuration_scenario"),
              ((Number) flat.get("test_configuration_min_turns")).intValue(),
              ((Number) flat.get("test_configuration_max_turns")).intValue());

      tests.add(
          new Test(
              null,
              testConfig,
              (String) flat.get("behavior"),
              (String) flat.get("category"),
              (String) flat.get("topic"),
              TestType.MULTI_TURN,
              null,
              null,
              null));
    }

    return tests;
  }

  public String getRenderedPrompt() {
    Map<String, Object> context = new HashMap<>();
    context.put("generation_prompt", config.getGenerationPrompt());
    context.put("behaviors", config.getBehaviors());
    context.put("categories", config.getCategories());
    context.put("topics", config.getTopics());
    context.put("additional_context", config.getAdditionalContext());
    // Usually missing num_tests for display purposes, defaulting to batchSize
    context.put("num_tests", batchSize);
    return renderTemplate("multi_turn_synthesizer.jinja", context);
  }
}
