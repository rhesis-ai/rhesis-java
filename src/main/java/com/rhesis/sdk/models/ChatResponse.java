package com.rhesis.sdk.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.HashMap;
import java.util.Map;

// The backend returns a direct JSON map of the parsed schema when using schema generation
public class ChatResponse {
    private final Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    public void addProperty(String name, Object value) {
        properties.put(name, value);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
