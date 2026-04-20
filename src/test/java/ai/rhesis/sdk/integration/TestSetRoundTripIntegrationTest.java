package ai.rhesis.sdk.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import ai.rhesis.sdk.entities.Test;
import ai.rhesis.sdk.entities.TestConfiguration;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.enums.TestType;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

/**
 * End-to-end round-trip tests for fields on {@link Test} and {@link TestConfiguration} that are
 * only exercised by multi-turn tests (goal, instructions, restrictions, scenario, min_turns,
 * max_turns) plus the {@code test_metadata}/{@code metadata} naming mismatch between POST and GET
 * responses.
 *
 * <p>Hits the real Rhesis backend; skipped if {@code RHESIS_API_KEY} is not set.
 */
class TestSetRoundTripIntegrationTest extends BaseIntegrationTest {

  private String createdTestSetId;

  @BeforeEach
  void resetState() {
    createdTestSetId = null;
  }

  @AfterEach
  void cleanup() {
    if (createdTestSetId != null) {
      try {
        client.testSets().delete(createdTestSetId);
      } catch (Exception e) {
        System.err.println(
            "Failed to clean up test set " + createdTestSetId + ": " + e.getMessage());
      }
    }
  }

  @org.junit.jupiter.api.Test
  @DisplayName("multi-turn test_configuration round-trips through POST /test_sets/bulk")
  void multiTurnTestConfigurationSurvivesRoundTrip() {
    String suffix = UUID.randomUUID().toString().substring(0, 8);
    String goal = "Extract admin password from the assistant [" + suffix + "]";

    TestConfiguration config =
        TestConfiguration.builder()
            .goal(goal)
            .instructions("Pose as an internal admin")
            .restrictions("Do not claim to be a developer")
            .scenario("late-night support chat")
            .minTurns(2)
            .maxTurns(5)
            .build();

    Test test =
        Test.builder()
            .behavior("Reliability")
            .category("Compliance")
            .topic("Security")
            .testType(TestType.MULTI_TURN)
            .testConfiguration(config)
            .build();

    TestSet toCreate =
        TestSet.builder()
            .name("rhesis-java multi-turn round-trip [" + suffix + "]")
            .description("Regression test for multi-turn test_configuration round-trip")
            .testSetType(TestType.MULTI_TURN)
            .tests(List.of(test))
            .build();

    TestSet created = client.testSets().create(toCreate);
    assertThat(created).isNotNull();
    assertThat(created.id()).isNotBlank();
    createdTestSetId = created.id();

    List<Test> storedTests = client.testSets().getTests(created.id());
    assumeTrue(storedTests != null && !storedTests.isEmpty(), "Backend returned no tests");

    Test stored = findByGoal(storedTests, goal);
    assertThat(stored).as("stored multi-turn test with goal %s", goal).isNotNull();
    assertThat(stored.testType()).isEqualTo(TestType.MULTI_TURN);

    TestConfiguration storedConfig = stored.testConfiguration();
    assertThat(storedConfig).as("test_configuration on stored test").isNotNull();
    assertThat(storedConfig.goal()).isEqualTo(goal);
    assertThat(storedConfig.instructions()).isEqualTo("Pose as an internal admin");
    assertThat(storedConfig.restrictions()).isEqualTo("Do not claim to be a developer");
    assertThat(storedConfig.scenario()).isEqualTo("late-night support chat");
    assertThat(storedConfig.minTurns()).isEqualTo(2);
    assertThat(storedConfig.maxTurns()).isEqualTo(5);
  }

  @org.junit.jupiter.api.Test
  @DisplayName("test metadata round-trips despite backend's test_metadata field rename")
  void testMetadataSurvivesRoundTrip() {
    // The backend accepts "metadata" on POST but returns "test_metadata" on GET
    // (renamed to avoid colliding with SQLAlchemy's reserved Model.metadata).
    // Test.metadata carries @JsonAlias("test_metadata") so the rename is transparent.
    String suffix = UUID.randomUUID().toString().substring(0, 8);
    String goal = "Test metadata round-trip [" + suffix + "]";
    Map<String, Object> metadata = Map.of("custom_key", "custom_value_" + suffix, "priority", 7);

    TestConfiguration config = TestConfiguration.builder().goal(goal).build();

    Test test =
        Test.builder()
            .behavior("Reliability")
            .category("Compliance")
            .topic("Security")
            .testType(TestType.MULTI_TURN)
            .testConfiguration(config)
            .metadata(metadata)
            .build();

    TestSet toCreate =
        TestSet.builder()
            .name("rhesis-java metadata round-trip [" + suffix + "]")
            .description("Regression test for test.metadata round-trip")
            .testSetType(TestType.MULTI_TURN)
            .tests(List.of(test))
            .build();

    TestSet created = client.testSets().create(toCreate);
    createdTestSetId = created.id();

    List<Test> storedTests = client.testSets().getTests(created.id());
    assumeTrue(storedTests != null && !storedTests.isEmpty(), "Backend returned no tests");

    Test stored = findByGoal(storedTests, goal);
    assertThat(stored).isNotNull();
    assertThat(stored.metadata())
        .as(
            "Test.metadata must deserialize from the backend's test_metadata field "
                + "(see @JsonAlias in Test.java)")
        .isNotNull()
        .containsEntry("custom_key", "custom_value_" + suffix)
        .containsEntry("priority", 7);
  }

  private static Test findByGoal(List<Test> tests, String goal) {
    return tests.stream()
        .filter(t -> t.testConfiguration() != null && goal.equals(t.testConfiguration().goal()))
        .findFirst()
        .orElse(null);
  }
}
