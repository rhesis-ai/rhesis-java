package com.rhesis.sdk.unit.models;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.rhesis.sdk.RhesisClient;
import com.rhesis.sdk.models.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RhesisNativeModelClientTest {

  private static WireMockServer wireMockServer;
  private static RhesisNativeModelClient client;

  @BeforeAll
  static void setUp() {
    wireMockServer = new WireMockServer(8090);
    wireMockServer.start();
    WireMock.configureFor("localhost", 8090);

    RhesisClient rhesisClient =
        RhesisClient.builder().baseUrl("http://localhost:8090").apiKey("test-key").build();
    client = rhesisClient.models();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.stop();
  }

  @Test
  void testChatCompletion() {
    String jsonResponse =
        """
                {
                  "tests": [
                    {
                      "test_configuration_goal": "Goal",
                      "test_configuration_instructions": "Instructions",
                      "test_configuration_restrictions": "Restrictions",
                      "test_configuration_scenario": "Scenario",
                      "test_configuration_min_turns": 1,
                      "test_configuration_max_turns": 3,
                      "behavior": "Behavior 1",
                      "category": "Category 1",
                      "topic": "Topic 1"
                    }
                  ]
                }
                """;

    stubFor(
        post(urlEqualTo("/services/generate/content"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .withHeader("Content-Type", containing("application/json"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonResponse)));

    ChatRequest request = new ChatRequest("Say hello", 0.7, 100, null);

    ChatResponse response = client.chat(request);

    assertThat(response.getProperties()).containsKey("tests");
  }
}
