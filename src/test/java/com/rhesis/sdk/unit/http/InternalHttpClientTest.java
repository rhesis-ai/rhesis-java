package com.rhesis.sdk.unit.http;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.rhesis.sdk.exceptions.RhesisApiException;
import com.rhesis.sdk.http.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class InternalHttpClientTest {

  private static WireMockServer wireMockServer;
  private static InternalHttpClient client;

  @BeforeAll
  static void setUp() {
    wireMockServer = new WireMockServer(8091);
    wireMockServer.start();
    WireMock.configureFor("localhost", 8091);

    client = new InternalHttpClient("http://localhost:8091", "test-key");
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.stop();
  }

  @Test
  void testApiErrorHandling() {
    String errorJson = "{\"error\": \"Unauthorized\", \"message\": \"Invalid API key\"}";

    stubFor(
        get(urlEqualTo("/test-endpoint"))
            .willReturn(
                aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", "application/json")
                    .withBody(errorJson)));

    assertThatThrownBy(() -> client.get("/test-endpoint", Object.class))
        .isInstanceOf(RhesisApiException.class)
        .hasMessageStartingWith("API request failed")
        .satisfies(
            e -> {
              RhesisApiException apiException = (RhesisApiException) e;
              org.assertj.core.api.Assertions.assertThat(apiException.getStatusCode())
                  .isEqualTo(401);
              org.assertj.core.api.Assertions.assertThat(apiException.getResponseBody())
                  .isEqualTo(errorJson);
            });
  }

  @Test
  void testServerErrorHandling() {
    stubFor(
        get(urlEqualTo("/test-endpoint"))
            .willReturn(aResponse().withStatus(500).withBody("Internal Server Error")));

    assertThatThrownBy(() -> client.get("/test-endpoint", Object.class))
        .isInstanceOf(RhesisApiException.class)
        .hasMessageStartingWith("API request failed")
        .satisfies(
            e -> {
              RhesisApiException apiException = (RhesisApiException) e;
              org.assertj.core.api.Assertions.assertThat(apiException.getStatusCode())
                  .isEqualTo(500);
              org.assertj.core.api.Assertions.assertThat(apiException.getResponseBody())
                  .isEqualTo("Internal Server Error");
            });
  }
}
