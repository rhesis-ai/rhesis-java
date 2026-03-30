package ai.rhesis.sdk.clients;

import ai.rhesis.sdk.entities.TestResult;
import ai.rhesis.sdk.entities.TestRun;
import ai.rhesis.sdk.entities.stats.TestRunStats;
import ai.rhesis.sdk.enums.TestRunStatsMode;
import ai.rhesis.sdk.http.InternalHttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.LinkedHashMap;
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
   * Get aggregated test run statistics with all sections.
   *
   * @return typed TestRunStats with all sections populated
   */
  public TestRunStats stats() {
    return stats(TestRunStatsMode.ALL, null);
  }

  /**
   * Get aggregated test run statistics with the given mode.
   *
   * @param mode controls which sections the backend populates
   * @return typed TestRunStats
   */
  public TestRunStats stats(TestRunStatsMode mode) {
    return stats(mode, null);
  }

  /**
   * Get statistics scoped to specific test run IDs.
   *
   * @param testRunIds list of test run IDs to filter by
   * @return typed TestRunStats
   */
  public TestRunStats stats(List<String> testRunIds) {
    Map<String, Object> params = new LinkedHashMap<>();
    if (testRunIds != null && !testRunIds.isEmpty()) {
      params.put("test_run_ids", testRunIds);
    }
    return stats(TestRunStatsMode.ALL, params);
  }

  /**
   * Get aggregated test run statistics with full control over mode and filters.
   *
   * @param mode controls which sections the backend populates
   * @param params optional filter parameters. Supported keys: "months", "top", "test_run_ids"
   *     (List), "user_ids" (List), "endpoint_ids" (List), "test_set_ids" (List), "status_list"
   *     (List), "start_date", "end_date"
   * @return typed TestRunStats
   */
  public TestRunStats stats(TestRunStatsMode mode, Map<String, Object> params) {
    StringBuilder path = new StringBuilder("/test_runs/stats?mode=");
    path.append(encode(mode.getValue()));

    if (params != null) {
      for (Map.Entry<String, Object> entry : params.entrySet()) {
        Object value = entry.getValue();
        if (value instanceof List<?> listVal) {
          for (Object item : listVal) {
            path.append("&").append(encode(entry.getKey())).append("=").append(encode(item.toString()));
          }
        } else {
          path.append("&").append(encode(entry.getKey())).append("=").append(encode(value.toString()));
        }
      }
    }

    return httpClient.get(path.toString(), TestRunStats.class);
  }

  private static String encode(String value) {
    return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
  }
}
