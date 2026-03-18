package com.rhesis.sdk.synthesizers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.rhesis.sdk.RhesisClient;
import com.rhesis.sdk.entities.synthesizers.SynthesizerConfig;
import com.rhesis.sdk.entities.synthesizers.SynthesizerResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SynthesizerClientTest {

  private static WireMockServer wireMockServer;
  private static SynthesizerClient client;

  @BeforeAll
  static void setUp() {
    wireMockServer = new WireMockServer(8089);
    wireMockServer.start();
    WireMock.configureFor("localhost", 8089);

    RhesisClient rhesisClient =
        RhesisClient.builder().baseUrl("http://localhost:8089/v1").apiKey("test-key").build();
    client = rhesisClient.synthesizers();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.stop();
  }

  @Test
  void testCreateSynthesizer() {
    stubFor(
        post(urlEqualTo("/v1/synthesizers"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"id\":\"synth-123\",\"name\":\"Test Synth\",\"description\":\"Desc\",\"base_model\":\"model-xyz\",\"status\":\"READY\"}")));

    SynthesizerConfig config = new SynthesizerConfig("Test Synth", "Desc", "model-xyz");
    SynthesizerResponse response = client.create(config);

    assertThat(response.id()).isEqualTo("synth-123");
    assertThat(response.name()).isEqualTo("Test Synth");
    assertThat(response.baseModel()).isEqualTo("model-xyz");
  }

  @Test
  void testGetSynthesizer() {
    stubFor(
        get(urlEqualTo("/v1/synthesizers/synth-123"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"id\":\"synth-123\",\"name\":\"Test Synth\",\"description\":\"Desc\",\"base_model\":\"model-xyz\",\"status\":\"READY\"}")));

    SynthesizerResponse response = client.get("synth-123");

    assertThat(response.id()).isEqualTo("synth-123");
    assertThat(response.name()).isEqualTo("Test Synth");
  }
}
