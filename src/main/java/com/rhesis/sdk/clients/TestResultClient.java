package com.rhesis.sdk.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rhesis.sdk.entities.TestResult;
import com.rhesis.sdk.http.InternalHttpClient;

import java.util.List;

public class TestResultClient {
  private final InternalHttpClient httpClient;

  public TestResultClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public List<TestResult> list() {
    return httpClient.get("/test_results/", new TypeReference<List<TestResult>>() {});
  }

  public TestResult get(String id) {
    return httpClient.get("/test_results/" + id, TestResult.class);
  }
}
