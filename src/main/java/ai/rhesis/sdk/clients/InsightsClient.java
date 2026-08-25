package ai.rhesis.sdk.clients;

import ai.rhesis.sdk.entities.InsightsIdsResponse;
import ai.rhesis.sdk.entities.InsightsResponse;
import ai.rhesis.sdk.http.InternalHttpClient;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class InsightsClient {
  private final InternalHttpClient httpClient;

  public InsightsClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  /**
   * Run an insights aggregation query.
   *
   * @param entity registry entity: "test_result", "metric", "test_run", or "test"
   * @param groupBy dimensions to group by (e.g. "requirement", "category", "topic")
   * @param measures measures to compute (e.g. "count", "pass_rate", "passed", "failed")
   * @param filters optional filter parameters keyed by filter name
   * @return InsightsResponse with entity, dimensions, measures, and rows
   */
  public InsightsResponse get(
      String entity,
      List<String> groupBy,
      List<String> measures,
      Map<String, List<String>> filters) {
    StringBuilder path = new StringBuilder("/insights/?entity=");
    path.append(encode(entity));

    if (groupBy != null) {
      for (String dim : groupBy) {
        path.append("&group_by=").append(encode(dim));
      }
    }
    if (measures != null) {
      for (String measure : measures) {
        path.append("&measures=").append(encode(measure));
      }
    }
    if (filters != null) {
      for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
        for (String value : entry.getValue()) {
          path.append("&").append(encode(entry.getKey())).append("=").append(encode(value));
        }
      }
    }

    return httpClient.get(path.toString(), InsightsResponse.class);
  }

  /**
   * Run an insights aggregation query with default measures (count).
   *
   * @param entity registry entity
   * @param groupBy dimensions to group by
   * @return InsightsResponse
   */
  public InsightsResponse get(String entity, List<String> groupBy) {
    return get(entity, groupBy, List.of("count"), null);
  }

  /**
   * Resolve distinct entity IDs matching insights filters.
   *
   * @param entity registry entity
   * @param outcome "pass", "fail", or "all"
   * @param filters optional filter parameters
   * @return InsightsIdsResponse with entity and ids
   */
  public InsightsIdsResponse ids(String entity, String outcome, Map<String, List<String>> filters) {
    StringBuilder path = new StringBuilder("/insights/ids?entity=");
    path.append(encode(entity));
    path.append("&outcome=").append(encode(outcome != null ? outcome : "all"));

    if (filters != null) {
      for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
        for (String value : entry.getValue()) {
          path.append("&").append(encode(entry.getKey())).append("=").append(encode(value));
        }
      }
    }

    return httpClient.get(path.toString(), InsightsIdsResponse.class);
  }

  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
