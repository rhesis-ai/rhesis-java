package com.rhesis.sdk.synthesizers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemaBuilder {

  public static Map<String, Object> buildFlatTestsSchema(
      Map<String, Map<String, Object>> properties, List<String> requiredFields) {
    Map<String, Object> items = new HashMap<>();
    items.put("type", "object");
    items.put("properties", properties);
    items.put("required", requiredFields);

    Map<String, Object> testsProp = new HashMap<>();
    testsProp.put("type", "array");
    testsProp.put("items", items);

    Map<String, Object> schemaProps = new HashMap<>();
    schemaProps.put("tests", testsProp);

    Map<String, Object> jsonSchema = new HashMap<>();
    jsonSchema.put("name", "FlatTests");
    jsonSchema.put(
        "schema",
        Map.of("type", "object", "properties", schemaProps, "required", List.of("tests")));
    jsonSchema.put("strict", true);

    Map<String, Object> rootSchema = new HashMap<>();
    rootSchema.put("type", "json_schema");
    rootSchema.put("json_schema", jsonSchema);

    return rootSchema;
  }

  public static Map<String, Object> buildSingleTurnSchema() {
    Map<String, Map<String, Object>> properties = new HashMap<>();
    properties.put("prompt_content", Map.of("type", "string"));
    properties.put("prompt_expected_response", Map.of("type", "string"));
    properties.put("prompt_language_code", Map.of("type", "string"));
    properties.put("behavior", Map.of("type", "string"));
    properties.put("category", Map.of("type", "string"));
    properties.put("topic", Map.of("type", "string"));

    return buildFlatTestsSchema(
        properties,
        List.of(
            "prompt_content",
            "prompt_expected_response",
            "prompt_language_code",
            "behavior",
            "category",
            "topic"));
  }

  public static Map<String, Object> buildMultiTurnSchema() {
    Map<String, Map<String, Object>> properties = new HashMap<>();
    properties.put("test_configuration_goal", Map.of("type", "string"));
    properties.put("test_configuration_instructions", Map.of("type", "string"));
    properties.put("test_configuration_restrictions", Map.of("type", "string"));
    properties.put("test_configuration_scenario", Map.of("type", "string"));
    properties.put("test_configuration_min_turns", Map.of("type", "integer"));
    properties.put("test_configuration_max_turns", Map.of("type", "integer"));
    properties.put("behavior", Map.of("type", "string"));
    properties.put("category", Map.of("type", "string"));
    properties.put("topic", Map.of("type", "string"));

    return buildFlatTestsSchema(
        properties,
        List.of(
            "test_configuration_goal",
            "test_configuration_instructions",
            "test_configuration_restrictions",
            "test_configuration_scenario",
            "test_configuration_min_turns",
            "test_configuration_max_turns",
            "behavior",
            "category",
            "topic"));
  }
}
