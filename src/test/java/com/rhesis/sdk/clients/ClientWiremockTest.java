package com.rhesis.sdk.clients;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.rhesis.sdk.RhesisClient;
import com.rhesis.sdk.entities.TestSet;
import com.rhesis.sdk.enums.TestType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ClientWiremockTest {
  private static WireMockServer wireMockServer;
  private static TestClient testClient;
  private static TestSetClient testSetClient;

  @BeforeAll
  static void setUp() {
    wireMockServer = new WireMockServer(8089);
    wireMockServer.start();
    WireMock.configureFor("localhost", 8089);

    RhesisClient rhesisClient =
        RhesisClient.builder().baseUrl("http://localhost:8089/v1").apiKey("test-key").build();
    testClient = rhesisClient.tests();
    testSetClient = rhesisClient.testSets();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.stop();
  }

  @Test
  void testGetTest() {
    stubFor(
        get(urlEqualTo("/v1/tests/t-123"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"id\":\"t-123\",\"test_type\":\"SINGLE_TURN\",\"behavior\":\"b1\"}")));

    com.rhesis.sdk.entities.Test response = testClient.get("t-123");
    assertThat(response.id()).isEqualTo("t-123");
    assertThat(response.testType()).isEqualTo(TestType.SINGLE_TURN);
    assertThat(response.behavior()).isEqualTo("b1");
  }

  @Test
  void testGetTestSet() {
    stubFor(
        get(urlEqualTo("/v1/test-sets/ts-123"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"id\":\"ts-123\",\"name\":\"My TestSet\",\"test_set_type\":\"MULTI_TURN\"}")));

    TestSet response = testSetClient.get("ts-123");
    assertThat(response.id()).isEqualTo("ts-123");
    assertThat(response.name()).isEqualTo("My TestSet");
    assertThat(response.testSetType()).isEqualTo(TestType.MULTI_TURN);
  }
}
