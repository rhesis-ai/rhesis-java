package ai.rhesis.sdk.examples;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.TestRun;
import ai.rhesis.sdk.entities.stats.MetricStats;
import ai.rhesis.sdk.entities.stats.TestResultStats;
import ai.rhesis.sdk.entities.stats.TestRunSummary;
import ai.rhesis.sdk.entities.stats.TimelineData;
import ai.rhesis.sdk.enums.TestResultStatsMode;
import java.util.List;
import java.util.Map;

public class TestResultStatsExample {
  public static void main(String[] args) {
    RhesisClient client = RhesisClient.builder().apiKey(System.getenv("RHESIS_API_KEY")).build();

    // --- Full test result stats ---
    System.out.println("=== Test Result Stats (all) ===");
    TestResultStats stats = client.testResults().stats();

    if (stats.overallPassRates() != null) {
      System.out.println("Total results: " + stats.overallPassRates().total());
      System.out.println("Passed:        " + stats.overallPassRates().passed());
      System.out.println("Failed:        " + stats.overallPassRates().failed());
      System.out.println("Pass rate:     " + stats.overallPassRates().passRate() + "%");
    }

    // --- Metric pass rates ---
    if (stats.metricPassRates() != null) {
      System.out.println("\n=== Metric Pass Rates ===");
      for (Map.Entry<String, MetricStats> entry : stats.metricPassRates().entrySet()) {
        MetricStats m = entry.getValue();
        System.out.printf(
            "  %-20s total=%d  passed=%d  failed=%d  rate=%.1f%%%n",
            entry.getKey(), m.total(), m.passed(), m.failed(), m.passRate());
      }
    }

    // --- Behavior breakdown ---
    System.out.println("\n=== Behavior Pass Rates ===");
    TestResultStats behaviorStats = client.testResults().stats(TestResultStatsMode.BEHAVIOR);
    if (behaviorStats.behaviorPassRates() != null) {
      for (Map.Entry<String, MetricStats> entry : behaviorStats.behaviorPassRates().entrySet()) {
        System.out.printf(
            "  %-25s rate=%.1f%% (%d/%d)%n",
            entry.getKey(),
            entry.getValue().passRate(),
            entry.getValue().passed(),
            entry.getValue().total());
      }
    }

    // --- Category breakdown ---
    System.out.println("\n=== Category Pass Rates ===");
    TestResultStats categoryStats = client.testResults().stats(TestResultStatsMode.CATEGORY);
    if (categoryStats.categoryPassRates() != null) {
      for (Map.Entry<String, MetricStats> entry : categoryStats.categoryPassRates().entrySet()) {
        System.out.printf("  %-25s rate=%.1f%%%n", entry.getKey(), entry.getValue().passRate());
      }
    }

    // --- Topic breakdown ---
    System.out.println("\n=== Topic Pass Rates ===");
    TestResultStats topicStats = client.testResults().stats(TestResultStatsMode.TOPIC);
    if (topicStats.topicPassRates() != null) {
      for (Map.Entry<String, MetricStats> entry : topicStats.topicPassRates().entrySet()) {
        System.out.printf("  %-25s rate=%.1f%%%n", entry.getKey(), entry.getValue().passRate());
      }
    }

    // --- Timeline ---
    if (stats.timeline() != null) {
      System.out.println("\n=== Timeline ===");
      for (TimelineData point : stats.timeline()) {
        System.out.printf(
            "  %s  overall: %d/%d (%.1f%%)%n",
            point.date(),
            point.overall().passed(),
            point.overall().total(),
            point.overall().passRate());
      }
    }

    // --- Per-run summary ---
    if (stats.testRunSummary() != null) {
      System.out.println("\n=== Per-Run Summary ===");
      for (TestRunSummary run : stats.testRunSummary()) {
        System.out.printf(
            "  [%s] %s — %d tests, rate=%.1f%%%n",
            run.id(), run.name(), run.totalTests(), run.overall().passRate());
      }
    }

    // --- Filtered by a specific test run ---
    List<TestRun> runs = client.testRuns().list();
    if (!runs.isEmpty()) {
      System.out.println("\n=== Results for run: " + runs.get(0).name() + " ===");
      TestResultStats runStats =
          client
              .testResults()
              .stats(TestResultStatsMode.ALL, Map.of("test_run_ids", List.of(runs.get(0).id())));

      if (runStats.overallPassRates() != null) {
        System.out.println("Pass rate: " + runStats.overallPassRates().passRate() + "%");
      }
      if (runStats.metricPassRates() != null) {
        System.out.println("Metrics evaluated: " + runStats.metricPassRates().size());
      }
    }

    // --- Metadata ---
    if (stats.metadata() != null) {
      System.out.println("\n=== Metadata ===");
      System.out.println("Period:          " + stats.metadata().period());
      System.out.println("Total runs:      " + stats.metadata().totalTestRuns());
      System.out.println("Total results:   " + stats.metadata().totalTestResults());
      System.out.println("Metrics:         " + stats.metadata().availableMetrics());
      System.out.println("Behaviors:       " + stats.metadata().availableBehaviors());
      System.out.println("Categories:      " + stats.metadata().availableCategories());
      System.out.println("Topics:          " + stats.metadata().availableTopics());
    }
  }
}
