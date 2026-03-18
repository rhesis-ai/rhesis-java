package com.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record Topic(
    @JsonProperty("id") String id,
    @NotBlank @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @JsonProperty("metadata") Map<String, Object> metadata) {}
