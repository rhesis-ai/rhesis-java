package com.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rhesis.sdk.enums.ConnectionType;
import java.util.Map;

public record Endpoint(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @JsonProperty("connection_type") ConnectionType connectionType,
    @JsonProperty("url") String url,
    @JsonProperty("project_id") String projectId,
    @JsonProperty("method") String method,
    @JsonProperty("endpoint_path") String endpointPath,
    @JsonProperty("request_headers") Map<String, String> requestHeaders,
    @JsonProperty("query_params") Map<String, Object> queryParams,
    @JsonProperty("request_mapping") Map<String, Object> requestMapping,
    @JsonProperty("response_mapping") Map<String, String> responseMapping,
    @JsonProperty("auth_token") String authToken) implements BaseEntity<Endpoint> {

    @JsonIgnore
    @Override
    public String getEndpointPath() {
        return "/endpoints";
    }
}
