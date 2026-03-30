package ai.rhesis.sdk.clients;

import ai.rhesis.sdk.entities.ExecutionRequest;
import ai.rhesis.sdk.entities.TestRun;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.enums.ExecutionMode;
import ai.rhesis.sdk.http.InternalHttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;

public class TestSetClient {
  private final InternalHttpClient httpClient;

  public TestSetClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public TestSet get(String id) {
    return httpClient.get("/test_sets/" + id, TestSet.class);
  }

  public List<TestSet> list() {
    return httpClient.get("/test_sets/", new TypeReference<List<TestSet>>() {});
  }

  public TestSet create(TestSet testSet) {
    return httpClient.post("/test_sets/bulk", testSet, TestSet.class);
  }

  public List<ai.rhesis.sdk.entities.Test> getTests(String id) {
    return getTests(id, 0, 100);
  }

  public List<ai.rhesis.sdk.entities.Test> getTests(String id, int skip, int limit) {
    return httpClient.get(
        "/test_sets/" + id + "/tests?skip=" + skip + "&limit=" + limit,
        new TypeReference<List<ai.rhesis.sdk.entities.Test>>() {});
  }

  public void delete(String id) {
    httpClient.delete("/test_sets/" + id);
  }

  /**
   * Execute a test set against the given endpoint.
   *
   * @param testSetId the test set ID
   * @param endpointId the endpoint ID to execute tests against
   * @return the execution submission response
   */
  public Map<String, Object> execute(String testSetId, String endpointId) {
    return execute(testSetId, endpointId, ExecutionMode.PARALLEL, null);
  }

  /**
   * Execute a test set against the given endpoint.
   *
   * @param testSetId the test set ID
   * @param endpointId the endpoint ID to execute tests against
   * @param mode execution mode (PARALLEL or SEQUENTIAL)
   * @param metrics optional list of metrics for this execution; each map should contain at least an
   *     "id" key and optionally "name" and "scope"
   * @return the execution submission response
   */
  public Map<String, Object> execute(
      String testSetId,
      String endpointId,
      ExecutionMode mode,
      List<Map<String, Object>> metrics) {
    if (testSetId == null) {
      throw new IllegalArgumentException("testSetId must be provided");
    }
    if (endpointId == null) {
      throw new IllegalArgumentException("endpointId must be provided");
    }

    ExecutionRequest body = ExecutionRequest.forExecution(mode, metrics);
    String path = "/test_sets/" + testSetId + "/execute/" + endpointId;
    return httpClient.post(path, body, new TypeReference<Map<String, Object>>() {});
  }

  /**
   * Re-score outputs from an existing test run without calling the endpoint again.
   *
   * @param testSetId the test set ID
   * @param endpointId the endpoint the original run was executed against
   * @param referenceTestRunId the test run whose outputs to re-score
   * @return the execution submission response
   */
  public Map<String, Object> rescore(
      String testSetId, String endpointId, String referenceTestRunId) {
    return rescore(testSetId, endpointId, referenceTestRunId, ExecutionMode.PARALLEL, null);
  }

  /**
   * Re-score outputs from an existing test run without calling the endpoint again.
   *
   * @param testSetId the test set ID
   * @param endpointId the endpoint the original run was executed against
   * @param referenceTestRunId the test run whose outputs to re-score
   * @param mode execution mode (PARALLEL or SEQUENTIAL)
   * @param metrics optional list of metrics for re-scoring
   * @return the execution submission response
   */
  public Map<String, Object> rescore(
      String testSetId,
      String endpointId,
      String referenceTestRunId,
      ExecutionMode mode,
      List<Map<String, Object>> metrics) {
    if (testSetId == null) {
      throw new IllegalArgumentException("testSetId must be provided");
    }
    if (endpointId == null) {
      throw new IllegalArgumentException("endpointId must be provided");
    }
    if (referenceTestRunId == null) {
      throw new IllegalArgumentException("referenceTestRunId must be provided");
    }

    ExecutionRequest body = ExecutionRequest.forRescore(mode, metrics, referenceTestRunId);
    String path = "/test_sets/" + testSetId + "/execute/" + endpointId;
    return httpClient.post(path, body, new TypeReference<Map<String, Object>>() {});
  }

  /**
   * Get the most recent completed test run for this test set and endpoint.
   *
   * @param testSetId the test set ID
   * @param endpointId the endpoint to look up the last run for
   * @return the last run summary, or null if no completed run exists
   */
  public TestRun lastRun(String testSetId, String endpointId) {
    if (testSetId == null) {
      throw new IllegalArgumentException("testSetId must be provided");
    }
    if (endpointId == null) {
      throw new IllegalArgumentException("endpointId must be provided");
    }

    String path = "/test_sets/" + testSetId + "/last-run/" + endpointId;
    return httpClient.get(path, TestRun.class);
  }

  /**
   * Get metrics associated with this test set.
   *
   * @param testSetId the test set ID
   * @return list of metric maps
   */
  public List<Map<String, Object>> getMetrics(String testSetId) {
    if (testSetId == null) {
      throw new IllegalArgumentException("testSetId must be provided");
    }
    String path = "/test_sets/" + testSetId + "/metrics";
    return httpClient.get(path, new TypeReference<List<Map<String, Object>>>() {});
  }

  /**
   * Add a metric to this test set.
   *
   * @param testSetId the test set ID
   * @param metricId the metric ID to add
   * @return the updated list of metrics on this test set
   */
  public List<Map<String, Object>> addMetric(String testSetId, String metricId) {
    if (testSetId == null) {
      throw new IllegalArgumentException("testSetId must be provided");
    }
    if (metricId == null) {
      throw new IllegalArgumentException("metricId must be provided");
    }
    String path = "/test_sets/" + testSetId + "/metrics/" + metricId;
    return httpClient.post(
        path, Map.of(), new TypeReference<List<Map<String, Object>>>() {});
  }

  /**
   * Remove a metric from this test set.
   *
   * @param testSetId the test set ID
   * @param metricId the metric ID to remove
   */
  public void removeMetric(String testSetId, String metricId) {
    if (testSetId == null) {
      throw new IllegalArgumentException("testSetId must be provided");
    }
    if (metricId == null) {
      throw new IllegalArgumentException("metricId must be provided");
    }
    httpClient.delete("/test_sets/" + testSetId + "/metrics/" + metricId);
  }

  /**
   * Associate existing tests with this test set.
   *
   * @param testSetId the test set ID
   * @param testIds list of test IDs to associate
   * @return association result
   */
  public Map<String, Object> addTests(String testSetId, List<String> testIds) {
    if (testSetId == null) {
      throw new IllegalArgumentException("testSetId must be provided");
    }
    if (testIds == null || testIds.isEmpty()) {
      throw new IllegalArgumentException("testIds must be provided and non-empty");
    }
    String path = "/test_sets/" + testSetId + "/associate";
    Map<String, Object> body = Map.of("test_ids", testIds);
    return httpClient.post(path, body, new TypeReference<Map<String, Object>>() {});
  }

  /**
   * Remove test associations from this test set.
   *
   * @param testSetId the test set ID
   * @param testIds list of test IDs to disassociate
   * @return disassociation result
   */
  public Map<String, Object> removeTests(String testSetId, List<String> testIds) {
    if (testSetId == null) {
      throw new IllegalArgumentException("testSetId must be provided");
    }
    if (testIds == null || testIds.isEmpty()) {
      throw new IllegalArgumentException("testIds must be provided and non-empty");
    }
    String path = "/test_sets/" + testSetId + "/disassociate";
    Map<String, Object> body = Map.of("test_ids", testIds);
    return httpClient.post(path, body, new TypeReference<Map<String, Object>>() {});
  }
}
