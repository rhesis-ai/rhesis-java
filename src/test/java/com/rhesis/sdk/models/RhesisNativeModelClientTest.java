package com.rhesis.sdk.models;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.rhesis.sdk.RhesisClient;
import com.rhesis.sdk.models.ChatRequest;
import com.rhesis.sdk.models.ChatResponse;
import java.util.List;
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
        RhesisClient.builder().baseUrl("http://localhost:8090/v1").apiKey("test-key").build();
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
                  "id": "chatcmpl-123",
                  "model": "rhesis-model-v1",
                  "choices": [
                    {
                      "index": 0,
                      "message": {
                        "role": "assistant",
                        "content": "Hello, world!"
                      },
                      "finish_reason": "stop"
                    }
                  ]
                }
                """;

    stubFor(
        post(urlEqualTo("/v1/chat/completions"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .withHeader("Content-Type", containing("application/json"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(jsonResponse)));

    ChatRequest request =
        new ChatRequest(
            "rhesis-model-v1", List.of(new ChatRequest.Message("user", "Say hello")), 0.7, 100);

    ChatResponse response = client.chat(request);

    assertThat(response.id()).isEqualTo("chatcmpl-123");
    assertThat(response.model()).isEqualTo("rhesis-model-v1");
    assertThat(response.choices()).hasSize(1);
    assertThat(response.choices().get(0).message().content()).isEqualTo("Hello, world!");
    assertThat(response.choices().get(0).message().role()).isEqualTo("assistant");
    assertThat(response.choices().get(0).finishReason()).isEqualTo("stop");
  }
}
