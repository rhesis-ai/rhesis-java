package com.rhesis.sdk.clients;

import com.rhesis.sdk.entities.Endpoint;
import com.rhesis.sdk.http.InternalHttpClient;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;

public class EndpointClient {
  private final InternalHttpClient httpClient;

  public EndpointClient(InternalHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public Endpoint get(String id) {
    return httpClient.get("/endpoints/" + id, Endpoint.class);
  }

  public List<Endpoint> list() {
    return httpClient.get("/endpoints/", new TypeReference<List<Endpoint>>() {});
  }

  public Endpoint create(Endpoint endpoint) {
    return httpClient.post("/endpoints/", endpoint, Endpoint.class);
  }

  public void delete(String id) {
    httpClient.delete("/endpoints/" + id);
  }

  public Map<String, Object> invoke(String id, String input, String conversationId) {
    Map<String, Object> body = new java.util.HashMap<>();
    body.put("input", input);
    if (conversationId != null) {
      body.put("conversation_id", conversationId);
    }
    return httpClient.post("/endpoints/" + id + "/invoke", body, new TypeReference<Map<String, Object>>() {});
  }
}
