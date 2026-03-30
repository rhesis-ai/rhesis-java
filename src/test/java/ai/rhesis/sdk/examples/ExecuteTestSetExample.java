package ai.rhesis.sdk.examples;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.Endpoint;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.enums.ExecutionMode;
import java.util.List;
import java.util.Map;

public class ExecuteTestSetExample {
  public static void main(String[] args) {
    RhesisClient client = RhesisClient.builder().apiKey(System.getenv("RHESIS_API_KEY")).build();

    // Find a test set and endpoint to execute against
    List<TestSet> testSets = client.testSets().list();
    List<Endpoint> endpoints = client.endpoints().list();

    if (testSets.isEmpty() || endpoints.isEmpty()) {
      System.err.println("Need at least one test set and one endpoint to run this example.");
      return;
    }

    TestSet testSet = testSets.get(0);
    Endpoint endpoint = endpoints.get(0);

    System.out.println("Test Set: " + testSet.name() + " (" + testSet.id() + ")");
    System.out.println("Endpoint: " + endpoint.name() + " (" + endpoint.id() + ")");

    // Execute the test set with default parallel mode
    System.out.println("\nExecuting test set (parallel mode)...");
    Map<String, Object> result = client.testSets().execute(testSet.id(), endpoint.id());
    System.out.println("Execution submitted: " + result);

    // Execute with sequential mode
    System.out.println("\nExecuting test set (sequential mode)...");
    Map<String, Object> seqResult =
        client.testSets().execute(testSet.id(), endpoint.id(), ExecutionMode.SEQUENTIAL, null);
    System.out.println("Execution submitted: " + seqResult);

    // Execute with specific metrics
    System.out.println("\nExecuting test set with custom metrics...");
    List<Map<String, Object>> metrics = List.of(Map.of("id", "your-metric-id", "name", "Accuracy"));
    Map<String, Object> metricResult =
        client.testSets().execute(testSet.id(), endpoint.id(), ExecutionMode.PARALLEL, metrics);
    System.out.println("Execution submitted: " + metricResult);
  }
}
