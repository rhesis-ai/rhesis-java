package com.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;

public record Prompt(
    @JsonProperty("id") String id,
    @NotBlank @JsonProperty("content") String content,
    @JsonProperty("role") String role,
    @JsonProperty("metadata") Map<String, Object> metadata) {}
