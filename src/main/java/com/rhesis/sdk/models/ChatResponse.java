package com.rhesis.sdk.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ChatResponse(
    @JsonProperty("id") String id,
    @JsonProperty("model") String model,
    @JsonProperty("choices") List<Choice> choices) {
  public record Choice(
      @JsonProperty("index") Integer index,
      @JsonProperty("message") Message message,
      @JsonProperty("finish_reason") String finishReason) {}

  public record Message(
      @JsonProperty("role") String role, @JsonProperty("content") String content) {}
}
