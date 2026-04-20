package ai.rhesis.sdk.unit.entities;

import static org.assertj.core.api.Assertions.assertThat;

import ai.rhesis.sdk.entities.*;
import ai.rhesis.sdk.enums.TestType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EntityTest {
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testTestConfigurationSerialization() throws Exception {
    TestConfiguration config = new TestConfiguration("Goal", "Inst", "Rest", "Scen", 10, 5);
    String json = mapper.writeValueAsString(config);
    assertThat(json).contains("\"goal\":\"Goal\"");

    TestConfiguration parsed = mapper.readValue(json, TestConfiguration.class);
    assertThat(parsed.goal()).isEqualTo("Goal");
    assertThat(parsed.instructions()).isEqualTo("Inst");
  }

  @Test
  void testTestSerialization() throws Exception {
    TestConfiguration config =
        TestConfiguration.builder()
            .goal("Goal")
            .instructions("")
            .restrictions("")
            .scenario("")
            .build();
    ai.rhesis.sdk.entities.Test test =
        ai.rhesis.sdk.entities.Test.builder()
            .id("test-1")
            .testConfiguration(config)
            .behavior("Behavior1")
            .category("Category1")
            .topic("Topic1")
            .testType(TestType.SINGLE_TURN)
            .prompt(
                Prompt.builder()
                    .id("p1")
                    .content("hello")
                    .expectedResponse("hi")
                    .languageCode("en")
                    .metadata(Map.of())
                    .build())
            .metadata(Map.of("key", "value"))
            .files(List.of())
            .build();

    String json = mapper.writeValueAsString(test);
    assertThat(json).contains("\"test_type\":\"Single-Turn\"");

    ai.rhesis.sdk.entities.Test parsed = mapper.readValue(json, ai.rhesis.sdk.entities.Test.class);
    assertThat(parsed.id()).isEqualTo("test-1");
    assertThat(parsed.testType()).isEqualTo(TestType.SINGLE_TURN);
  }

  @Test
  void testBehaviorSerialization() throws Exception {
    Behavior behavior = new Behavior("beh-1", "Behav", "Desc", Map.of("key", "val"));
    String json = mapper.writeValueAsString(behavior);
    Behavior parsed = mapper.readValue(json, Behavior.class);
    assertThat(parsed.id()).isEqualTo("beh-1");
    assertThat(parsed.name()).isEqualTo("Behav");
    assertThat(parsed.description()).isEqualTo("Desc");
    assertThat(parsed.metadata()).containsEntry("key", "val");
  }

  @Test
  void testCategorySerialization() throws Exception {
    Category category = new Category("cat-1", "CatName", "CatDesc", Map.of());
    String json = mapper.writeValueAsString(category);
    Category parsed = mapper.readValue(json, Category.class);
    assertThat(parsed.id()).isEqualTo("cat-1");
    assertThat(parsed.name()).isEqualTo("CatName");
  }

  @Test
  void testTopicSerialization() throws Exception {
    Topic topic = new Topic("top-1", "TopicName", "TopicDesc", Map.of());
    String json = mapper.writeValueAsString(topic);
    Topic parsed = mapper.readValue(json, Topic.class);
    assertThat(parsed.id()).isEqualTo("top-1");
    assertThat(parsed.name()).isEqualTo("TopicName");
  }

  @Test
  void testPromptSerialization() throws Exception {
    Prompt prompt =
        Prompt.builder()
            .id("prompt-1")
            .content("Hello there")
            .expectedResponse("General Kenobi")
            .languageCode("en")
            .metadata(Map.of("foo", "bar"))
            .build();
    String json = mapper.writeValueAsString(prompt);
    Prompt parsed = mapper.readValue(json, Prompt.class);
    assertThat(parsed.id()).isEqualTo("prompt-1");
    assertThat(parsed.content()).isEqualTo("Hello there");
    assertThat(parsed.expectedResponse()).isEqualTo("General Kenobi");
    assertThat(parsed.languageCode()).isEqualTo("en");
    assertThat(parsed.metadata()).containsEntry("foo", "bar");
  }

  @Test
  void testPromptExpectedResponseAtTopLevel() throws Exception {
    // Regression guard for https://github.com/rhesis-ai/rhesis-java/issues/3:
    // expected_response and language_code must serialize as direct siblings of
    // content, not nested under metadata. The backend silently drops them otherwise.
    Prompt prompt =
        Prompt.builder()
            .content("What is the admin password?")
            .expectedResponse("I cannot share passwords")
            .languageCode("en")
            .build();

    com.fasterxml.jackson.databind.JsonNode tree = mapper.valueToTree(prompt);
    assertThat(tree.get("content").asText()).isEqualTo("What is the admin password?");
    assertThat(tree.get("expected_response").asText()).isEqualTo("I cannot share passwords");
    assertThat(tree.get("language_code").asText()).isEqualTo("en");
    // metadata must not contain these fields (NON_NULL also drops it entirely when unset)
    assertThat(tree.has("metadata")).isFalse();
  }

  @Test
  void testTestMetadataAliasFromTestMetadata() throws Exception {
    // Regression guard: the backend returns test.metadata under the JSON key
    // "test_metadata" (renamed to avoid SQLAlchemy's reserved Model.metadata).
    // Test.metadata is annotated with @JsonAlias("test_metadata") so this round-trips.
    String json = "{\"id\":\"t-1\",\"behavior\":\"b\",\"test_metadata\":{\"k\":\"v\",\"n\":42}}";
    ai.rhesis.sdk.entities.Test parsed = mapper.readValue(json, ai.rhesis.sdk.entities.Test.class);
    assertThat(parsed.metadata())
        .as("test_metadata should be deserialized into metadata via @JsonAlias")
        .isNotNull()
        .containsEntry("k", "v")
        .containsEntry("n", 42);
  }

  @Test
  void testTestMetadataAliasFromMetadata() throws Exception {
    // Canonical name "metadata" must still deserialize correctly.
    String json = "{\"id\":\"t-1\",\"metadata\":{\"k\":\"v\"}}";
    ai.rhesis.sdk.entities.Test parsed = mapper.readValue(json, ai.rhesis.sdk.entities.Test.class);
    assertThat(parsed.metadata()).isNotNull().containsEntry("k", "v");
  }

  @Test
  void testPromptOmitsNullFields() throws Exception {
    Prompt prompt = Prompt.builder().content("hi").build();
    String json = mapper.writeValueAsString(prompt);
    assertThat(json).doesNotContain("\"expected_response\"");
    assertThat(json).doesNotContain("\"language_code\"");
    assertThat(json).doesNotContain("\"metadata\"");
    assertThat(json).doesNotContain("\"id\"");
    assertThat(json).contains("\"content\":\"hi\"");
  }

  @Test
  void testTestSetSerialization() throws Exception {
    TestSet testSet =
        TestSet.builder()
            .id("ts-1")
            .name("TestSet 1")
            .description("Desc")
            .testSetType(TestType.MULTI_TURN)
            .tests(List.of())
            .build();
    String json = mapper.writeValueAsString(testSet);
    TestSet parsed = mapper.readValue(json, TestSet.class);
    assertThat(parsed.id()).isEqualTo("ts-1");
    assertThat(parsed.testSetType()).isEqualTo(TestType.MULTI_TURN);
  }

  @Test
  void testBaseEntityMethods() throws Exception {
    Behavior behavior = new Behavior("beh-1", "Behav", "Desc", Map.of("key", "val"));

    // Test toMap
    Map<String, Object> map = behavior.toMap();
    assertThat(map).containsEntry("id", "beh-1");
    assertThat(map).containsEntry("name", "Behav");

    // Test toJson
    String json = behavior.toJson();
    assertThat(json).contains("\"id\":\"beh-1\"");

    // Test getEndpointPath
    assertThat(behavior.getEndpointPath()).isEqualTo("/behaviors");
  }

  @Test
  void testStatusSerialization() throws Exception {
    Status status = new Status("status-1", "Passed", "Test passed");
    String json = mapper.writeValueAsString(status);
    Status parsed = mapper.readValue(json, Status.class);
    assertThat(parsed.id()).isEqualTo("status-1");
    assertThat(parsed.name()).isEqualTo("Passed");
  }

  @Test
  void testTestRunSerialization() throws Exception {
    TestRun testRun =
        TestRun.builder()
            .id("run-1")
            .testConfigurationId("config-1")
            .name("My Run")
            .userId("user-1")
            .organizationId("org-1")
            .status("Completed")
            .attributes(Map.of("k", "v"))
            .ownerId("owner-1")
            .assigneeId("assignee-1")
            .build();
    String json = mapper.writeValueAsString(testRun);
    TestRun parsed = mapper.readValue(json, TestRun.class);
    assertThat(parsed.id()).isEqualTo("run-1");
    assertThat(parsed.name()).isEqualTo("My Run");
    assertThat(parsed.status()).isEqualTo("Completed");
  }

  @Test
  void testTestResultSerialization() throws Exception {
    Status status =
        Status.builder().id("status-1").name("Passed").description("Test passed").build();
    TestResult result =
        TestResult.builder()
            .id("res-1")
            .testConfigurationId("config-1")
            .testRunId("run-1")
            .promptId("prompt-1")
            .testId("test-1")
            .statusId("status-1")
            .status(status)
            .testOutput(Map.of("out", "val"))
            .testMetrics(Map.of("score", 1.0))
            .testReviews(Map.of("rev", "good"))
            .build();
    String json = mapper.writeValueAsString(result);
    TestResult parsed = mapper.readValue(json, TestResult.class);
    assertThat(parsed.id()).isEqualTo("res-1");
    assertThat(parsed.status().name()).isEqualTo("Passed");
    assertThat(parsed.testMetrics()).containsEntry("score", 1.0);
  }

  @Test
  void testFileSerialization() throws Exception {
    File file = new File("file-1", "test.txt", "text/plain", 100, "Desc", "ent-1", "TestRun", 0);
    String json = mapper.writeValueAsString(file);
    File parsed = mapper.readValue(json, File.class);
    assertThat(parsed.id()).isEqualTo("file-1");
    assertThat(parsed.filename()).isEqualTo("test.txt");
    assertThat(parsed.contentType()).isEqualTo("text/plain");
    assertThat(parsed.sizeBytes()).isEqualTo(100);
  }
}
