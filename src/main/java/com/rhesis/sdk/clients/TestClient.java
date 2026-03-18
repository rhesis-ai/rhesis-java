package com.rhesis.sdk.clients;

import com.rhesis.sdk.entities.Test;
import com.rhesis.sdk.http.InternalHttpClient;

public class TestClient {
  private final InternalHttpClient httpClient;

  public TestClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public Test get(String id) {
    return httpClient.get("/tests/" + id, Test.class);
  }

  public Test create(Test test) {
    return httpClient.post("/tests", test, Test.class);
  }
}
