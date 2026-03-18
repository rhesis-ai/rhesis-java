package com.rhesis.sdk;

import com.rhesis.sdk.clients.EndpointClient;
import com.rhesis.sdk.clients.ProjectClient;
import com.rhesis.sdk.clients.TestClient;
import com.rhesis.sdk.clients.TestResultClient;
import com.rhesis.sdk.clients.TestRunClient;
import com.rhesis.sdk.clients.TestSetClient;
import com.rhesis.sdk.clients.FileClient;
import com.rhesis.sdk.http.InternalHttpClient;
import com.rhesis.sdk.models.RhesisNativeModelClient;
import com.rhesis.sdk.synthesizers.SynthesizerClient;

public class RhesisClient {
  private static volatile RhesisClient defaultInstance;

  private final InternalHttpClient httpClient;
  private final RhesisNativeModelClient models;
  private final SynthesizerClient synthesizers;
  private final TestClient tests;
  private final TestSetClient testSets;
  private final EndpointClient endpoints;
  private final ProjectClient projects;
  private final TestRunClient testRuns;
  private final TestResultClient testResults;
  private final FileClient files;

  RhesisClient(String baseUrl, String apiKey) {
    this.httpClient = new InternalHttpClient(baseUrl, apiKey);
    this.models = new RhesisNativeModelClient(this.httpClient);
    this.synthesizers = new SynthesizerClient(this.httpClient);
    this.tests = new TestClient(this.httpClient);
    this.testSets = new TestSetClient(this.httpClient);
    this.endpoints = new EndpointClient(this.httpClient);
    this.projects = new ProjectClient(this.httpClient);
    this.testRuns = new TestRunClient(this.httpClient);
    this.testResults = new TestResultClient(this.httpClient);
    this.files = new FileClient(this.httpClient);
  }

  public static RhesisClientBuilder builder() {
    return new RhesisClientBuilder();
  }

  public static RhesisClient getDefault() {
    if (defaultInstance == null) {
      synchronized (RhesisClient.class) {
        if (defaultInstance == null) {
          defaultInstance = builder().build();
        }
      }
    }
    return defaultInstance;
  }

  public static void setDefault(RhesisClient client) {
    defaultInstance = client;
  }

  public InternalHttpClient getHttpClient() {
    return httpClient;
  }

  public RhesisNativeModelClient models() {
    return models;
  }

  public ProjectClient projects() {
    return projects;
  }

  public EndpointClient endpoints() {
    return endpoints;
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

  public TestRunClient testRuns() {
    return testRuns;
  }

  public TestResultClient testResults() {
    return testResults;
  }

  public FileClient files() {
    return files;
  }
}
