package ai.rhesis.sdk.entities;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class NameStringDeserializer extends JsonDeserializer<String> {
  @Override
  public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    if (node.isObject() && node.has("name")) {
      return node.get("name").asText();
    } else if (node.isTextual()) {
      return node.asText();
    } else if (node.isNull()) {
      return null;
    }
    return node.toString();
  }
}
