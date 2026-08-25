package ai.rhesis.sdk.clients;

import ai.rhesis.sdk.entities.InsightsIdsResponse;
import ai.rhesis.sdk.entities.InsightsQuery;
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
   * @param query the insights query (entity, group_by, measures, filters, date range)
   * @return InsightsResponse with entity, dimensions, measures, and rows
   */
  public InsightsResponse get(InsightsQuery query) {
    StringBuilder path = new StringBuilder("/insights/?entity=");
    path.append(encode(query.entity()));
    appendList(path, "group_by", query.groupBy());
    appendList(path, "measures", query.measures());
    appendFilters(path, query.filters());
    appendDateRange(path, query);
    return httpClient.get(path.toString(), InsightsResponse.class);
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
    InsightsQuery.Builder builder = InsightsQuery.builder(entity);
    if (groupBy != null) builder.groupBy(groupBy);
    if (measures != null) builder.measures(measures);
    if (filters != null) builder.filters(filters);
    return get(builder.build());
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
   * @param query the insights query (entity, filters, date range)
   * @param outcome "pass", "fail", or "all"
   * @return InsightsIdsResponse with entity and ids
   */
  public InsightsIdsResponse ids(InsightsQuery query, String outcome) {
    StringBuilder path = new StringBuilder("/insights/ids?entity=");
    path.append(encode(query.entity()));
    path.append("&outcome=").append(encode(outcome != null ? outcome : "all"));
    appendFilters(path, query.filters());
    appendDateRange(path, query);
    return httpClient.get(path.toString(), InsightsIdsResponse.class);
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
    InsightsQuery.Builder builder = InsightsQuery.builder(entity);
    if (filters != null) builder.filters(filters);
    return ids(builder.build(), outcome);
  }

  private static void appendList(StringBuilder path, String param, List<String> values) {
    if (values != null) {
      for (String value : values) {
        path.append("&").append(param).append("=").append(encode(value));
      }
    }
  }

  private static void appendFilters(StringBuilder path, Map<String, List<String>> filters) {
    if (filters != null) {
      for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
        for (String value : entry.getValue()) {
          path.append("&").append(encode(entry.getKey())).append("=").append(encode(value));
        }
      }
    }
  }

  private static void appendDateRange(StringBuilder path, InsightsQuery query) {
    if (query.months() != null) {
      path.append("&months=").append(query.months());
    }
    if (query.startDate() != null) {
      path.append("&start_date=").append(encode(query.startDate()));
    }
    if (query.endDate() != null) {
      path.append("&end_date=").append(encode(query.endDate()));
    }
  }

  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
