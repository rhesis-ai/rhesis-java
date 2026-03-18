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
    TestConfiguration config = new TestConfiguration("Goal", "", "", "", null, null);
    ai.rhesis.sdk.entities.Test test =
        new ai.rhesis.sdk.entities.Test(
            "test-1",
            config,
            "Behavior1",
            "Category1",
            "Topic1",
            TestType.SINGLE_TURN,
            new Prompt("p1", "hello", "user", Map.of()),
            Map.of("key", "value"),
            List.of());

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
    Prompt prompt = new Prompt("prompt-1", "Hello there", "user", Map.of("foo", "bar"));
    String json = mapper.writeValueAsString(prompt);
    Prompt parsed = mapper.readValue(json, Prompt.class);
    assertThat(parsed.id()).isEqualTo("prompt-1");
    assertThat(parsed.role()).isEqualTo("user");
    assertThat(parsed.content()).isEqualTo("Hello there");
    assertThat(parsed.metadata()).containsEntry("foo", "bar");
  }

  @Test
  void testTestSetSerialization() throws Exception {
    TestSet testSet = new TestSet("ts-1", "TestSet 1", "Desc", TestType.MULTI_TURN, List.of());
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
        new TestRun(
            "run-1",
            "config-1",
            "My Run",
            "user-1",
            "org-1",
            ai.rhesis.sdk.enums.RunStatus.COMPLETED,
            Map.of("k", "v"),
            "owner-1",
            "assignee-1");
    String json = mapper.writeValueAsString(testRun);
    TestRun parsed = mapper.readValue(json, TestRun.class);
    assertThat(parsed.id()).isEqualTo("run-1");
    assertThat(parsed.name()).isEqualTo("My Run");
  }

  @Test
  void testTestResultSerialization() throws Exception {
    Status status = new Status("status-1", "Passed", "Test passed");
    TestResult result =
        new TestResult(
            "res-1",
            "config-1",
            "run-1",
            "prompt-1",
            "test-1",
            "status-1",
            status,
            Map.of("out", "val"),
            Map.of("score", 1.0),
            Map.of("rev", "good"));
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
