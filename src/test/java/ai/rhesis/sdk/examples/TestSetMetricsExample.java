package ai.rhesis.sdk.examples;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.TestSet;
import java.util.List;
import java.util.Map;

public class TestSetMetricsExample {
  public static void main(String[] args) {
    RhesisClient client = RhesisClient.builder().apiKey(System.getenv("RHESIS_API_KEY")).build();

    List<TestSet> testSets = client.testSets().list();
    if (testSets.isEmpty()) {
      System.err.println("No test sets found.");
      return;
    }

    TestSet testSet = testSets.get(0);
    System.out.println("Test Set: " + testSet.name() + " (" + testSet.id() + ")");

    // --- List current metrics ---
    System.out.println("\n=== Current Metrics ===");
    List<Map<String, Object>> metrics = client.testSets().getMetrics(testSet.id());
    if (metrics.isEmpty()) {
      System.out.println("No metrics assigned yet.");
    } else {
      for (Map<String, Object> metric : metrics) {
        System.out.printf("  [%s] %s%n", metric.get("id"), metric.get("name"));
      }
    }

    // --- Add a metric by ID ---
    // Uncomment and replace with a real metric ID to test:
    //
    // String metricId = "your-metric-uuid";
    // System.out.println("\nAdding metric " + metricId + "...");
    // List<Map<String, Object>> updated = client.testSets().addMetric(testSet.id(), metricId);
    // System.out.println("Metrics after add: " + updated);

    // --- Remove a metric ---
    // Uncomment and replace with a real metric ID to test:
    //
    // System.out.println("\nRemoving metric " + metricId + "...");
    // client.testSets().removeMetric(testSet.id(), metricId);
    // System.out.println("Metric removed.");

    // --- Associate / disassociate tests ---
    System.out.println("\n=== Test Association ===");
    List<ai.rhesis.sdk.entities.Test> tests = client.testSets().getTests(testSet.id());
    System.out.println("Current test count: " + tests.size());

    // Uncomment to associate tests by ID:
    //
    // List<String> testIds = List.of("test-uuid-1", "test-uuid-2");
    // Map<String, Object> addResult = client.testSets().addTests(testSet.id(), testIds);
    // System.out.println("Add result: " + addResult);
    //
    // Map<String, Object> removeResult = client.testSets().removeTests(testSet.id(), testIds);
    // System.out.println("Remove result: " + removeResult);
  }
}
