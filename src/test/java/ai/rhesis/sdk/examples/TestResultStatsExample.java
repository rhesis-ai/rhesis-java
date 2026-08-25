package ai.rhesis.sdk.examples;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.InsightsIdsResponse;
import ai.rhesis.sdk.entities.InsightsQuery;
import ai.rhesis.sdk.entities.InsightsResponse;
import java.util.List;
import java.util.Map;

public class TestResultStatsExample {
  public static void main(String[] args) {
    RhesisClient client = RhesisClient.builder().apiKey(System.getenv("RHESIS_API_KEY")).build();

    // --- Overall test result counts ---
    System.out.println("=== Test Result Count ===");
    InsightsResponse overall =
        client
            .insights()
            .get("test_result", List.of(), List.of("count", "pass_rate", "passed", "failed"), null);
    for (Map<String, Object> row : overall.rows()) {
      System.out.println("  " + row);
    }

    // --- Pass rates by requirement ---
    System.out.println("\n=== Pass Rates by Requirement ===");
    InsightsResponse byRequirement =
        client
            .insights()
            .get("test_result", List.of("requirement"), List.of("count", "pass_rate"), null);
    for (Map<String, Object> row : byRequirement.rows()) {
      System.out.printf(
          "  %-25s count=%s  pass_rate=%s%n",
          row.get("requirement"), row.get("count"), row.get("pass_rate"));
    }

    // --- Pass rates by category ---
    System.out.println("\n=== Pass Rates by Category ===");
    InsightsResponse byCategory =
        client
            .insights()
            .get("test_result", List.of("category"), List.of("count", "pass_rate"), null);
    for (Map<String, Object> row : byCategory.rows()) {
      System.out.printf(
          "  %-25s count=%s  pass_rate=%s%n",
          row.get("category"), row.get("count"), row.get("pass_rate"));
    }

    // --- Pass rates by topic ---
    System.out.println("\n=== Pass Rates by Topic ===");
    InsightsResponse byTopic =
        client.insights().get("test_result", List.of("topic"), List.of("count", "pass_rate"), null);
    for (Map<String, Object> row : byTopic.rows()) {
      System.out.printf(
          "  %-25s count=%s  pass_rate=%s%n",
          row.get("topic"), row.get("count"), row.get("pass_rate"));
    }

    // --- Results from the last 6 months ---
    System.out.println("\n=== Test Results (last 6 months) ===");
    InsightsQuery recentQuery =
        InsightsQuery.builder("test_result")
            .groupBy(List.of("requirement"))
            .measures(List.of("count", "pass_rate"))
            .months(6)
            .build();
    InsightsResponse recent = client.insights().get(recentQuery);
    for (Map<String, Object> row : recent.rows()) {
      System.out.printf(
          "  %-25s count=%s  pass_rate=%s%n",
          row.get("requirement"), row.get("count"), row.get("pass_rate"));
    }

    // --- Get IDs of failed test results ---
    System.out.println("\n=== Failed Test Result IDs ===");
    InsightsIdsResponse failedIds = client.insights().ids("test_result", "fail", null);
    System.out.println("Entity: " + failedIds.entity());
    System.out.println("Failed IDs: " + failedIds.ids().size());
    for (String id : failedIds.ids().subList(0, Math.min(5, failedIds.ids().size()))) {
      System.out.println("  " + id);
    }
  }
}
