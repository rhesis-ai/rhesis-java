package ai.rhesis.sdk.examples;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.Endpoint;
import ai.rhesis.sdk.entities.InsightsResponse;
import ai.rhesis.sdk.entities.TestResult;
import ai.rhesis.sdk.entities.TestRun;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.exceptions.RhesisApiException;
import java.util.List;
import java.util.Map;

public class TestRunWorkflowExample {
  public static void main(String[] args) {
    RhesisClient client = RhesisClient.builder().apiKey(System.getenv("RHESIS_API_KEY")).build();

    // --- List and inspect test runs ---
    System.out.println("=== Listing Test Runs ===");
    List<TestRun> runs = client.testRuns().list();
    System.out.println("Found " + runs.size() + " test runs");

    for (TestRun run : runs.subList(0, Math.min(3, runs.size()))) {
      System.out.printf("  [%s] %s - status: %s%n", run.id(), run.name(), run.status());
    }

    // --- Get test results for a run ---
    if (!runs.isEmpty()) {
      TestRun firstRun = runs.get(0);
      System.out.println("\n=== Test Results for: " + firstRun.name() + " ===");

      List<TestResult> results = client.testRuns().getTestResults(firstRun.id());
      System.out.println("Found " + results.size() + " test results");

      for (TestResult result : results.subList(0, Math.min(3, results.size()))) {
        System.out.printf(
            "  [%s] status: %s%n",
            result.id(), result.status() != null ? result.status().name() : "unknown");
      }
    }

    // --- Get test run insights ---
    System.out.println("\n=== Test Run Insights ===");
    InsightsResponse insights =
        client.insights().get("test_run", List.of("status"), List.of("count"), null);
    for (Map<String, Object> row : insights.rows()) {
      System.out.printf("  %-15s count=%s%n", row.get("status"), row.get("count"));
    }

    // --- Last run for a test set + endpoint pair ---
    List<TestSet> testSets = client.testSets().list();
    List<Endpoint> endpoints = client.endpoints().list();

    if (!testSets.isEmpty() && !endpoints.isEmpty()) {
      System.out.println("\n=== Last Run ===");
      try {
        TestRun lastRun = client.testSets().lastRun(testSets.get(0).id(), endpoints.get(0).id());
        System.out.printf(
            "Last run: %s (status: %s, pass rate: %s)%n",
            lastRun.name(), lastRun.status(), lastRun.passRate());
      } catch (RhesisApiException e) {
        if (e.getStatusCode() == 404) {
          System.out.println("No completed run found for this test set + endpoint pair.");
        } else {
          throw e;
        }
      }
    }

    // --- Rescore an existing run ---
    if (!testSets.isEmpty() && !endpoints.isEmpty() && !runs.isEmpty()) {
      System.out.println("\n=== Rescore ===");
      try {
        Map<String, Object> rescoreResult =
            client
                .testSets()
                .rescore(testSets.get(0).id(), endpoints.get(0).id(), runs.get(0).id());
        System.out.println("Rescore submitted: " + rescoreResult);
      } catch (RhesisApiException e) {
        System.out.println(
            "Rescore failed (expected if run doesn't belong to this pair): " + e.getMessage());
      }
    }
  }
}
