package ai.rhesis.sdk.examples;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.TestRun;
import ai.rhesis.sdk.entities.stats.StatusDistribution;
import ai.rhesis.sdk.entities.stats.TestRunStats;
import ai.rhesis.sdk.entities.stats.TestRunTimelineData;
import ai.rhesis.sdk.entities.stats.TestSetRunCount;
import ai.rhesis.sdk.enums.TestRunStatsMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestRunStatsExample {
  public static void main(String[] args) {
    RhesisClient client = RhesisClient.builder().apiKey(System.getenv("RHESIS_API_KEY")).build();

    // --- Full stats (all sections) ---
    System.out.println("=== Test Run Stats (all) ===");
    TestRunStats stats = client.testRuns().stats();

    if (stats.overallSummary() != null) {
      System.out.println("Total runs:       " + stats.overallSummary().totalRuns());
      System.out.println("Unique test sets: " + stats.overallSummary().uniqueTestSets());
      System.out.println("Unique executors: " + stats.overallSummary().uniqueExecutors());
      System.out.println("Pass rate:        " + stats.overallSummary().passRate() + "%");
      System.out.println("Most common:      " + stats.overallSummary().mostCommonStatus());
    }

    // --- Status distribution ---
    if (stats.statusDistribution() != null) {
      System.out.println("\n=== Status Distribution ===");
      for (StatusDistribution sd : stats.statusDistribution()) {
        System.out.printf("  %-12s %d (%.1f%%)%n", sd.status(), sd.count(), sd.percentage());
      }
    }

    // --- Most run test sets ---
    if (stats.mostRunTestSets() != null) {
      System.out.println("\n=== Most Run Test Sets ===");
      for (TestSetRunCount ts : stats.mostRunTestSets()) {
        System.out.printf("  %-30s %d runs%n", ts.testSetName(), ts.runCount());
      }
    }

    // --- Timeline ---
    if (stats.timeline() != null) {
      System.out.println("\n=== Timeline ===");
      for (TestRunTimelineData point : stats.timeline()) {
        System.out.printf("  %s  total_runs=%d%n", point.date(), point.totalRuns());
      }
    }

    // --- Summary-only mode (lighter payload) ---
    System.out.println("\n=== Summary Mode ===");
    TestRunStats summary = client.testRuns().stats(TestRunStatsMode.SUMMARY);
    if (summary.overallSummary() != null) {
      System.out.println("Total runs: " + summary.overallSummary().totalRuns());
    }

    // --- Stats scoped to specific runs ---
    List<TestRun> runs = client.testRuns().list();
    if (!runs.isEmpty()) {
      System.out.println("\n=== Stats for run: " + runs.get(0).name() + " ===");
      TestRunStats scoped = client.testRuns().stats(List.of(runs.get(0).id()));
      if (scoped.overallSummary() != null) {
        System.out.println("Pass rate: " + scoped.overallSummary().passRate() + "%");
      }
    }

    // --- Stats with custom filter params ---
    System.out.println("\n=== Stats (last 3 months) ===");
    Map<String, Object> params = new LinkedHashMap<>();
    params.put("months", 3);
    TestRunStats filtered = client.testRuns().stats(TestRunStatsMode.ALL, params);
    if (filtered.metadata() != null) {
      System.out.println("Period: " + filtered.metadata().period());
      System.out.println("Total runs: " + filtered.metadata().totalTestRuns());
    }
  }
}
