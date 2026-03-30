package ai.rhesis.sdk.examples;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.Endpoint;
import ai.rhesis.sdk.entities.TestResult;
import ai.rhesis.sdk.entities.TestRun;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.entities.stats.TestRunStats;
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

    // --- Get test run stats ---
    System.out.println("\n=== Test Run Stats ===");
    TestRunStats stats = client.testRuns().stats();
    if (stats.overallSummary() != null) {
      System.out.println("Total runs: " + stats.overallSummary().totalRuns());
      System.out.println("Pass rate: " + stats.overallSummary().passRate());
      System.out.println("Unique test sets: " + stats.overallSummary().uniqueTestSets());
    }

    // Stats scoped to specific runs
    if (!runs.isEmpty()) {
      System.out.println("\n=== Stats for first run ===");
      TestRunStats scopedStats = client.testRuns().stats(List.of(runs.get(0).id()));
      if (scopedStats.overallSummary() != null) {
        System.out.println("Scoped pass rate: " + scopedStats.overallSummary().passRate());
      }
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
        System.out.println("Rescore failed (expected if run doesn't belong to this pair): " + e.getMessage());
      }
    }
  }
}
