package com.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record TestRun(
    @JsonProperty("id") String id,
    @JsonProperty("test_configuration_id") String testConfigurationId,
    @JsonProperty("name") String name,
    @JsonProperty("user_id") String userId,
    @JsonProperty("organization_id") String organizationId,
    @JsonProperty("status") Object status, // Can be String enum or Status object
    @JsonProperty("attributes") Map<String, Object> attributes,
    @JsonProperty("owner_id") String ownerId,
    @JsonProperty("assignee_id") String assigneeId) implements BaseEntity<TestRun> {

    @JsonIgnore
    @Override
    public String getEndpointPath() {
        return "/test_runs";
    }
}
