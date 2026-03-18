package com.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rhesis.sdk.enums.TestType;
import java.util.List;

public record TestSet(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @JsonProperty("test_set_type") TestType testSetType,
    @JsonProperty("tests") List<Test> tests) {}
