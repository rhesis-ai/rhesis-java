package com.rhesis.sdk;

import com.rhesis.sdk.http.InternalHttpClient;
import com.rhesis.sdk.models.RhesisNativeModelClient;
import com.rhesis.sdk.synthesizers.SynthesizerClient;

public class RhesisClient {
  private final InternalHttpClient httpClient;
  private final RhesisNativeModelClient models;
  private final SynthesizerClient synthesizers;

  RhesisClient(String baseUrl, String apiKey) {
    this.httpClient = new InternalHttpClient(baseUrl, apiKey);
    this.models = new RhesisNativeModelClient(this.httpClient);
    this.synthesizers = new SynthesizerClient(this.httpClient);
  }

  public static RhesisClientBuilder builder() {
    return new RhesisClientBuilder();
  }

  public RhesisNativeModelClient models() {
    return models;
  }

  public SynthesizerClient synthesizers() {
    return synthesizers;
  }
}
