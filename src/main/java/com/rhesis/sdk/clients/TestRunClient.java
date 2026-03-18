package com.rhesis.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rhesis.sdk.entities.TestResult;
import com.rhesis.sdk.entities.TestRun;
import com.rhesis.sdk.http.InternalHttpClient;
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
