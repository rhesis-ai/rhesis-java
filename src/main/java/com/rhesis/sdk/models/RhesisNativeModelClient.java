package com.rhesis.sdk.models;

import com.rhesis.sdk.http.InternalHttpClient;

public class RhesisNativeModelClient implements ChatModelClient {
  private final InternalHttpClient httpClient;

  public RhesisNativeModelClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  @Override
  public ChatResponse chat(ChatRequest request) {
    return httpClient.post("/services/generate/content", request, ChatResponse.class);
  }
}
