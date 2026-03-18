package com.rhesis.sdk;

import com.rhesis.sdk.clients.TestClient;
import com.rhesis.sdk.clients.TestSetClient;
import com.rhesis.sdk.http.InternalHttpClient;
import com.rhesis.sdk.models.RhesisNativeModelClient;
import com.rhesis.sdk.synthesizers.SynthesizerClient;

public class RhesisClient {
  private final InternalHttpClient httpClient;
  private final RhesisNativeModelClient models;
  private final SynthesizerClient synthesizers;
  private final TestClient tests;
  private final TestSetClient testSets;

  RhesisClient(String baseUrl, String apiKey) {
    this.httpClient = new InternalHttpClient(baseUrl, apiKey);
    this.models = new RhesisNativeModelClient(this.httpClient);
    this.synthesizers = new SynthesizerClient(this.httpClient);
    this.tests = new TestClient(this.httpClient);
    this.testSets = new TestSetClient(this.httpClient);
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

  public TestClient tests() {
    return tests;
  }

  public TestSetClient testSets() {
    return testSets;
  }
}
