package com.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record Behavior(
    @JsonProperty("id") String id,
    @NotBlank @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @JsonProperty("metadata") Map<String, Object> metadata)
    implements BaseEntity<Behavior> {

  @JsonIgnore
  @Override
  public String getEndpointPath() {
    return "/behaviors";
  }
}
