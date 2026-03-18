package com.rhesis.sdk.entities;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhesis.sdk.enums.TestType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EntityTest {
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testTestConfigurationSerialization() throws Exception {
    TestConfiguration config = new TestConfiguration("Goal", "Inst", "Rest", "Scen", 10, 5);
    String json = mapper.writeValueAsString(config);
    assertThat(json).contains("\"goal\":\"Goal\"");

    TestConfiguration parsed = mapper.readValue(json, TestConfiguration.class);
    assertThat(parsed.goal()).isEqualTo("Goal");
    assertThat(parsed.instructions()).isEqualTo("Inst");
  }

  @Test
  void testTestSerialization() throws Exception {
    TestConfiguration config = new TestConfiguration("Goal", "", "", "", null, null);
    com.rhesis.sdk.entities.Test test =
        new com.rhesis.sdk.entities.Test(
            "test-1",
            config,
            "Behavior1",
            "Category1",
            "Topic1",
            TestType.SINGLE_TURN,
            new Prompt("p1", "hello", "user", Map.of()),
            Map.of("key", "value"),
            List.of());

    String json = mapper.writeValueAsString(test);
    assertThat(json).contains("\"test_type\":\"SINGLE_TURN\"");

    com.rhesis.sdk.entities.Test parsed =
        mapper.readValue(json, com.rhesis.sdk.entities.Test.class);
    assertThat(parsed.id()).isEqualTo("test-1");
    assertThat(parsed.testType()).isEqualTo(TestType.SINGLE_TURN);
  }
}
