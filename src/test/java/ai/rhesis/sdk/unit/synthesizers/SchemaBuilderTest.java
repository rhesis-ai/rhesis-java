package ai.rhesis.sdk.unit.synthesizers;

import static org.junit.jupiter.api.Assertions.*;

import ai.rhesis.sdk.synthesizers.SchemaBuilder;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SchemaBuilderTest {

  @Test
  @SuppressWarnings("unchecked")
  void singleTurnSchemaHasAdditionalPropertiesFalseAtAllLevels() {
    Map<String, Object> root = SchemaBuilder.buildSingleTurnSchema();
    Map<String, Object> jsonSchema = (Map<String, Object>) root.get("json_schema");
    Map<String, Object> schema = (Map<String, Object>) jsonSchema.get("schema");

    assertEquals(false, schema.get("additionalProperties"), "root schema object");

    Map<String, Object> props = (Map<String, Object>) schema.get("properties");
    Map<String, Object> tests = (Map<String, Object>) props.get("tests");
    Map<String, Object> items = (Map<String, Object>) tests.get("items");

    assertEquals(false, items.get("additionalProperties"), "test item object");
  }

  @Test
  @SuppressWarnings("unchecked")
  void multiTurnSchemaHasAdditionalPropertiesFalseAtAllLevels() {
    Map<String, Object> root = SchemaBuilder.buildMultiTurnSchema();
    Map<String, Object> jsonSchema = (Map<String, Object>) root.get("json_schema");
    Map<String, Object> schema = (Map<String, Object>) jsonSchema.get("schema");

    assertEquals(false, schema.get("additionalProperties"), "root schema object");

    Map<String, Object> props = (Map<String, Object>) schema.get("properties");
    Map<String, Object> tests = (Map<String, Object>) props.get("tests");
    Map<String, Object> items = (Map<String, Object>) tests.get("items");

    assertEquals(false, items.get("additionalProperties"), "test item object");
  }
}
