package com.rhesis.sdk.models;

import com.rhesis.sdk.entities.models.ChatRequest;
import com.rhesis.sdk.entities.models.ChatResponse;
import com.rhesis.sdk.http.InternalHttpClient;

public class RhesisNativeModelClient {
  private final InternalHttpClient httpClient;

  public RhesisNativeModelClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public ChatResponse chat(ChatRequest request) {
    return httpClient.post("/chat/completions", request, ChatResponse.class);
  }
}
