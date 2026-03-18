package com.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record TestConfiguration(
    @NotBlank @JsonProperty("goal") String goal,
    @JsonProperty("instructions") String instructions,
    @JsonProperty("restrictions") String restrictions,
    @JsonProperty("scenario") String scenario,
    @JsonProperty("max_turns") Integer maxTurns,
    @JsonProperty("min_turns") Integer minTurns) {
  public TestConfiguration {
    if (instructions == null) instructions = "";
    if (restrictions == null) restrictions = "";
    if (scenario == null) scenario = "";
  }
}
