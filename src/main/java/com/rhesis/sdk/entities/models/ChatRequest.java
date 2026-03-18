package com.rhesis.sdk.entities.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ChatRequest(
    @NotBlank @JsonProperty("model") String model,
    @NotEmpty @JsonProperty("messages") List<Message> messages,
    @JsonProperty("temperature") Double temperature,
    @JsonProperty("max_tokens") Integer maxTokens) {
  public record Message(
      @NotBlank @JsonProperty("role") String role,
      @NotBlank @JsonProperty("content") String content) {}
}
