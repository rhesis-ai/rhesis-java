package com.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Project(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("description") String description) implements BaseEntity<Project> {

    @JsonIgnore
    @Override
    public String getEndpointPath() {
        return "/projects";
    }
}
