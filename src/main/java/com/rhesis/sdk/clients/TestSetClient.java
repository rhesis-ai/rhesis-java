package com.rhesis.sdk.clients;

import com.rhesis.sdk.entities.TestSet;
import com.rhesis.sdk.http.InternalHttpClient;

public class TestSetClient {
  private final InternalHttpClient httpClient;

  public TestSetClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public TestSet get(String id) {
    return httpClient.get("/test-sets/" + id, TestSet.class);
  }

  public TestSet create(TestSet testSet) {
    return httpClient.post("/test-sets", testSet, TestSet.class);
  }
}
