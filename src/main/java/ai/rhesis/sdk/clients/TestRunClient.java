package ai.rhesis.sdk.clients;

import ai.rhesis.sdk.entities.TestResult;
import ai.rhesis.sdk.entities.TestRun;
import ai.rhesis.sdk.entities.stats.TestRunStats;
import ai.rhesis.sdk.enums.TestRunStatsMode;
import ai.rhesis.sdk.http.InternalHttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;

public class TestRunClient {
  private final InternalHttpClient httpClient;

  public TestRunClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public List<TestRun> list() {
    return httpClient.get("/test_runs/", new TypeReference<List<TestRun>>() {});
  }

  public TestRun get(String id) {
    return httpClient.get("/test_runs/" + id, TestRun.class);
  }

  public List<TestResult> getTestResults(String testRunId) {
    if (testRunId == null) {
      throw new IllegalArgumentException("testRunId must be provided");
    }
    String filter = "?$filter=test_run_id%20eq%20'" + testRunId + "'";
    return httpClient.get("/test_results/" + filter, new TypeReference<List<TestResult>>() {});
  }

  /**
   * @deprecated Use {@link ai.rhesis.sdk.clients.InsightsClient} with entity "test_run" instead.
   */
  @Deprecated
  public TestRunStats stats() {
    throw new UnsupportedOperationException(
        "TestRunClient.stats() has been removed. "
            + "Use client.insights().get(\"test_run\", ...) instead.");
  }

  /**
   * @deprecated Use {@link ai.rhesis.sdk.clients.InsightsClient} with entity "test_run" instead.
   */
  @Deprecated
  public TestRunStats stats(TestRunStatsMode mode) {
    throw new UnsupportedOperationException(
        "TestRunClient.stats() has been removed. "
            + "Use client.insights().get(\"test_run\", ...) instead.");
  }

  /**
   * @deprecated Use {@link ai.rhesis.sdk.clients.InsightsClient} with entity "test_run" instead.
   */
  @Deprecated
  public TestRunStats stats(List<String> testRunIds) {
    throw new UnsupportedOperationException(
        "TestRunClient.stats() has been removed. "
            + "Use client.insights().get(\"test_run\", ...) instead.");
  }

  /**
   * @deprecated Use {@link ai.rhesis.sdk.clients.InsightsClient} with entity "test_run" instead.
   */
  @Deprecated
  public TestRunStats stats(TestRunStatsMode mode, Map<String, Object> params) {
    throw new UnsupportedOperationException(
        "TestRunClient.stats() has been removed. "
            + "Use client.insights().get(\"test_run\", ...) instead.");
  }
}
