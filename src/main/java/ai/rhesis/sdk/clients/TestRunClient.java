package ai.rhesis.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import ai.rhesis.sdk.entities.TestResult;
import ai.rhesis.sdk.entities.TestRun;
import ai.rhesis.sdk.http.InternalHttpClient;
import java.util.List;

public class TestRunClient {
  private final InternalHttpClient httpClient;

  public TestRunClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public List<TestRun> list() {
    return httpClient.get("/test_runs/", new TypeReference<List<TestRun>>() {});
  }

  public TestRun get(String id) {
    return httpClient.get("/test_runs/" + id, TestRun.class);
  }

  public List<TestResult> getTestResults(String testRunId) {
    if (testRunId == null) {
      throw new IllegalArgumentException("testRunId must be provided");
    }
    String filter = "?$filter=test_run_id%20eq%20'" + testRunId + "'";
    return httpClient.get("/test_results/" + filter, new TypeReference<List<TestResult>>() {});
  }
}
