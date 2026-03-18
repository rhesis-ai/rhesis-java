package com.rhesis.sdk.synthesizers;

import com.rhesis.sdk.entities.synthesizers.SynthesizerConfig;
import com.rhesis.sdk.entities.synthesizers.SynthesizerResponse;
import com.rhesis.sdk.http.InternalHttpClient;

public class SynthesizerClient {
  private final InternalHttpClient httpClient;

  public SynthesizerClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public SynthesizerResponse create(SynthesizerConfig config) {
    return httpClient.post("/synthesizers", config, SynthesizerResponse.class);
  }

  public SynthesizerResponse get(String synthesizerId) {
    return httpClient.get("/synthesizers/" + synthesizerId, SynthesizerResponse.class);
  }
}
