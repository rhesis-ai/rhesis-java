package ai.rhesis.sdk.clients;

import ai.rhesis.sdk.entities.File;
import ai.rhesis.sdk.entities.TestResult;
import ai.rhesis.sdk.entities.stats.TestResultStats;
import ai.rhesis.sdk.enums.TestResultStatsMode;
import ai.rhesis.sdk.http.InternalHttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;

public class TestResultClient {
  private final InternalHttpClient httpClient;

  public TestResultClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public List<TestResult> list() {
    return httpClient.get("/test_results/", new TypeReference<List<TestResult>>() {});
  }

  public TestResult get(String id) {
    return httpClient.get("/test_results/" + id, TestResult.class);
  }

  public List<File> getFiles(String testResultId) {
    return httpClient.get(
        "/test_results/" + testResultId + "/files", new TypeReference<List<File>>() {});
  }

  /**
   * @deprecated Use {@link ai.rhesis.sdk.clients.InsightsClient} with entity "test_result" instead.
   */
  @Deprecated
  public TestResultStats stats() {
    throw new UnsupportedOperationException(
        "TestResultClient.stats() has been removed. "
            + "Use client.insights().get(\"test_result\", ...) instead.");
  }

  /**
   * @deprecated Use {@link ai.rhesis.sdk.clients.InsightsClient} with entity "test_result" instead.
   */
  @Deprecated
  public TestResultStats stats(TestResultStatsMode mode) {
    throw new UnsupportedOperationException(
        "TestResultClient.stats() has been removed. "
            + "Use client.insights().get(\"test_result\", ...) instead.");
  }

  /**
   * @deprecated Use {@link ai.rhesis.sdk.clients.InsightsClient} with entity "test_result" instead.
   */
  @Deprecated
  public TestResultStats stats(TestResultStatsMode mode, Map<String, Object> params) {
    throw new UnsupportedOperationException(
        "TestResultClient.stats() has been removed. "
            + "Use client.insights().get(\"test_result\", ...) instead.");
  }
}
