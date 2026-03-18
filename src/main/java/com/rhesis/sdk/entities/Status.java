package com.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Status(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("description") String description) implements BaseEntity<Status> {

    @JsonIgnore
    @Override
    public String getEndpointPath() {
        return "/statuses";
    }
}
