package ai.rhesis.sdk.examples;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.InsightsQuery;
import ai.rhesis.sdk.entities.InsightsResponse;
import java.util.List;
import java.util.Map;

public class TestRunStatsExample {
  public static void main(String[] args) {
    RhesisClient client = RhesisClient.builder().apiKey(System.getenv("RHESIS_API_KEY")).build();

    // --- Test run count ---
    System.out.println("=== Test Run Count ===");
    InsightsResponse runCount =
        client.insights().get("test_run", List.of(), List.of("count"), null);
    System.out.println("Dimensions: " + runCount.dimensions());
    System.out.println("Measures:   " + runCount.measures());
    for (Map<String, Object> row : runCount.rows()) {
      System.out.println("  " + row);
    }

    // --- Test runs grouped by status ---
    System.out.println("\n=== Test Runs by Status ===");
    InsightsResponse byStatus =
        client.insights().get("test_run", List.of("status"), List.of("count"), null);
    for (Map<String, Object> row : byStatus.rows()) {
      System.out.printf("  %-15s count=%s%n", row.get("status"), row.get("count"));
    }

    // --- Test runs from the last 3 months ---
    System.out.println("\n=== Test Runs (last 3 months) ===");
    InsightsQuery recentQuery =
        InsightsQuery.builder("test_run")
            .groupBy(List.of("status"))
            .measures(List.of("count"))
            .months(3)
            .build();
    InsightsResponse recent = client.insights().get(recentQuery);
    for (Map<String, Object> row : recent.rows()) {
      System.out.printf("  %-15s count=%s%n", row.get("status"), row.get("count"));
    }

    // --- Test runs with date range ---
    System.out.println("\n=== Test Runs (custom date range) ===");
    InsightsQuery dateRangeQuery =
        InsightsQuery.builder("test_run")
            .measures(List.of("count"))
            .startDate("2025-01-01")
            .endDate("2025-12-31")
            .build();
    InsightsResponse dateRange = client.insights().get(dateRangeQuery);
    for (Map<String, Object> row : dateRange.rows()) {
      System.out.println("  " + row);
    }
  }
}
