package com.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record TestConfiguration(
    @NotBlank @JsonProperty("goal") String goal,
    @JsonProperty("instructions") String instructions,
    @JsonProperty("restrictions") String restrictions,
    @JsonProperty("scenario") String scenario,
    @JsonProperty("max_turns") Integer maxTurns,
    @JsonProperty("min_turns") Integer minTurns) implements BaseEntity<TestConfiguration> {

  public TestConfiguration {
    if (instructions == null) instructions = "";
    if (restrictions == null) restrictions = "";
    if (scenario == null) scenario = "";
  }

  @JsonIgnore
  @Override
  public String id() {
    return null; // TestConfiguration doesn't have an ID, it's a sub-component of Test
  }

  @JsonIgnore
  @Override
  public String getEndpointPath() {
    return "/test_configurations"; // Or whatever path applies if this gets its own endpoint
  }
}
