package ai.rhesis.sdk.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.Endpoint;
import ai.rhesis.sdk.entities.InsightsResponse;
import ai.rhesis.sdk.entities.TestRun;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.enums.ExecutionMode;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestRunIntegrationTest extends BaseIntegrationTest {

  private static String testSetId;
  private static String endpointId;

  @BeforeAll
  static void setupGlobalClient() {
    RhesisClient.setDefault(client);

    List<TestSet> testSets = client.testSets().list();
    if (!testSets.isEmpty()) {
      testSetId = testSets.get(0).id();
    }

    List<Endpoint> endpoints = client.endpoints().list();
    if (!endpoints.isEmpty()) {
      endpointId = endpoints.get(0).id();
    }
  }

  @Test
  @Order(1)
  void testListTestRuns() {
    List<TestRun> runs = client.testRuns().list();
    assertThat(runs).isNotNull();
  }

  @Test
  @Order(2)
  void testGetTestRun() {
    List<TestRun> runs = client.testRuns().list();
    assumeTrue(!runs.isEmpty(), "No test runs available to fetch");

    TestRun run = client.testRuns().get(runs.get(0).id());
    assertThat(run).isNotNull();
    assertThat(run.id()).isEqualTo(runs.get(0).id());
    assertThat(run.name()).isNotNull();
  }

  @Test
  @Order(3)
  void testGetTestRunResults() {
    List<TestRun> runs = client.testRuns().list();
    assumeTrue(!runs.isEmpty(), "No test runs available to fetch results");

    var results = client.testRuns().getTestResults(runs.get(0).id());
    assertThat(results).isNotNull();
  }

  @Test
  @Order(4)
  void testInsightsTestRunCount() {
    InsightsResponse response =
        client.insights().get("test_run", List.of(), List.of("count"), null);
    assertThat(response).isNotNull();
    assertThat(response.entity()).isEqualTo("test_run");
    assertThat(response.measures()).contains("count");
  }

  @Test
  @Order(5)
  void testInsightsTestResultByRequirement() {
    InsightsResponse response =
        client
            .insights()
            .get("test_result", List.of("requirement"), List.of("count", "pass_rate"), null);
    assertThat(response).isNotNull();
    assertThat(response.entity()).isEqualTo("test_result");
    assertThat(response.dimensions()).contains("requirement");
  }

  @Test
  @Order(6)
  void testInsightsTestResultByCategory() {
    InsightsResponse response =
        client
            .insights()
            .get("test_result", List.of("category"), List.of("count", "pass_rate"), null);
    assertThat(response).isNotNull();
    assertThat(response.entity()).isEqualTo("test_result");
  }

  @Test
  @Order(7)
  void testInsightsWithFilters() {
    List<TestRun> runs = client.testRuns().list();
    assumeTrue(!runs.isEmpty(), "No test runs available for filtered insights");

    InsightsResponse response =
        client
            .insights()
            .get(
                "test_result",
                List.of("requirement"),
                List.of("count"),
                Map.of("test_run_ids", List.of(runs.get(0).id())));
    assertThat(response).isNotNull();
  }

  @Test
  @Order(8)
  void testInsightsIds() {
    var response = client.insights().ids("test_result", "all", null);
    assertThat(response).isNotNull();
    assertThat(response.entity()).isEqualTo("test_result");
    assertThat(response.ids()).isNotNull();
  }

  @Test
  @Order(20)
  void testLastRun() {
    assumeTrue(testSetId != null, "No test set available");
    assumeTrue(endpointId != null, "No endpoint available");

    try {
      TestRun lastRun = client.testSets().lastRun(testSetId, endpointId);
      if (lastRun != null) {
        assertThat(lastRun.id()).isNotNull();
      }
    } catch (ai.rhesis.sdk.exceptions.RhesisApiException e) {
      if (e.getStatusCode() == 404) {
        System.out.println("No last run found for test set + endpoint combination (expected)");
      } else {
        throw e;
      }
    }
  }

  @Test
  @Order(21)
  void testExecuteTestSet() {
    assumeTrue(testSetId != null, "No test set available");
    assumeTrue(endpointId != null, "No endpoint available");

    Map<String, Object> result =
        client.testSets().execute(testSetId, endpointId, ExecutionMode.PARALLEL, null);
    assertThat(result).isNotNull();
  }

  @Test
  @Order(22)
  void testGetTestSetMetrics() {
    assumeTrue(testSetId != null, "No test set available");

    List<Map<String, Object>> metrics = client.testSets().getMetrics(testSetId);
    assertThat(metrics).isNotNull();
  }

  @Test
  @Order(23)
  void testTestRunStatusDeserialization() {
    List<TestRun> runs = client.testRuns().list();
    assumeTrue(!runs.isEmpty(), "No test runs available");

    TestRun run = client.testRuns().get(runs.get(0).id());
    if (run.status() != null) {
      assertThat(run.status()).isInstanceOf(String.class);
      assertThat(run.status()).isNotEmpty();
    }
  }
}
