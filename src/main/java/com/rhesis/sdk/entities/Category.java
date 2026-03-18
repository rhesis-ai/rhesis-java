package com.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record Category(
    @JsonProperty("id") String id,
    @NotBlank @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @JsonProperty("metadata") Map<String, Object> metadata)
    implements BaseEntity<Category> {

  @JsonIgnore
  @Override
  public String getEndpointPath() {
    return "/categories";
  }
}
