package com.rhesis.sdk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.rhesis.sdk.enums.TestType;
import java.io.IOException;
import java.util.List;

public record TestSet(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("description") String description,
    @JsonProperty("test_set_type") @JsonDeserialize(using = TestTypeDeserializer.class)
        TestType testSetType,
    @JsonProperty("tests") List<Test> tests)
    implements BaseEntity<TestSet> {

  @JsonIgnore
  @Override
  public String getEndpointPath() {
    return "/test_sets";
  }

  public static class TestTypeDeserializer extends JsonDeserializer<TestType> {
    @Override
    public TestType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonNode node = p.getCodec().readTree(p);
      String value = null;
      if (node.isObject() && node.has("type_value")) {
        value = node.get("type_value").asText();
      } else if (node.isTextual()) {
        value = node.asText();
      }

      if (value != null) {
        // Convert "Multi-Turn" to "MULTI_TURN" etc to match the Java Enum names
        String normalized = value.toUpperCase(java.util.Locale.ROOT).replace("-", "_");
        try {
          return TestType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
          // Fallback to exactly what the enum is if normalized doesn't match
          return TestType.valueOf(value);
        }
      }
      return null;
    }
  }
}
