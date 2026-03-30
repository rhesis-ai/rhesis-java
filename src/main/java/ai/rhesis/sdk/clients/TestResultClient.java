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
   * Get aggregated test result statistics with all sections.
   *
   * @return typed TestResultStats with all sections populated
   */
  public TestResultStats stats() {
    return stats(TestResultStatsMode.ALL, null);
  }

  /**
   * Get aggregated test result statistics with the given mode.
   *
   * @param mode controls which sections the backend populates
   * @return typed TestResultStats
   */
  public TestResultStats stats(TestResultStatsMode mode) {
    return stats(mode, null);
  }

  /**
   * Get aggregated test result statistics with full control over mode and filters.
   *
   * <p>Supported filter keys:
   *
   * <ul>
   *   <li>{@code months} — number of months of historical data (default 6)
   *   <li>{@code test_run_id} — filter by a single test run ID
   *   <li>{@code test_run_ids} — filter by multiple test run IDs (List)
   *   <li>{@code test_set_ids} — filter by test set IDs (List)
   *   <li>{@code behavior_ids} — filter by behavior IDs (List)
   *   <li>{@code category_ids} — filter by category IDs (List)
   *   <li>{@code topic_ids} — filter by topic IDs (List)
   *   <li>{@code status_ids} — filter by test status IDs (List)
   *   <li>{@code test_ids} — filter by specific test IDs (List)
   *   <li>{@code test_type_ids} — filter by test type IDs (List)
   *   <li>{@code user_ids} — filter by test creator user IDs (List)
   *   <li>{@code assignee_ids} — filter by assignee user IDs (List)
   *   <li>{@code owner_ids} — filter by test owner user IDs (List)
   *   <li>{@code prompt_ids} — filter by prompt IDs (List)
   *   <li>{@code priority_min} — minimum priority (inclusive)
   *   <li>{@code priority_max} — maximum priority (inclusive)
   *   <li>{@code tags} — filter by tags (List)
   *   <li>{@code start_date} — start date (ISO format), overrides months
   *   <li>{@code end_date} — end date (ISO format), overrides months
   * </ul>
   *
   * @param mode controls which sections the backend populates
   * @param params optional filter parameters (may be null)
   * @return typed TestResultStats
   */
  public TestResultStats stats(TestResultStatsMode mode, Map<String, Object> params) {
    StringBuilder path = new StringBuilder("/test_results/stats?mode=");
    path.append(encode(mode.getValue()));

    if (params != null) {
      for (Map.Entry<String, Object> entry : params.entrySet()) {
        Object value = entry.getValue();
        if (value instanceof List<?> listVal) {
          for (Object item : listVal) {
            path.append("&")
                .append(encode(entry.getKey()))
                .append("=")
                .append(encode(item.toString()));
          }
        } else {
          path.append("&")
              .append(encode(entry.getKey()))
              .append("=")
              .append(encode(value.toString()));
        }
      }
    }

    return httpClient.get(path.toString(), TestResultStats.class);
  }

  private static String encode(String value) {
    return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
  }
}
