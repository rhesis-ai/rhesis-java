package com.rhesis.sdk.unit.clients;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.rhesis.sdk.RhesisClient;
import com.rhesis.sdk.clients.*;
import com.rhesis.sdk.entities.File;
import com.rhesis.sdk.entities.TestResult;
import com.rhesis.sdk.entities.TestRun;
import com.rhesis.sdk.entities.TestSet;
import com.rhesis.sdk.enums.RunStatus;
import com.rhesis.sdk.enums.TestType;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ClientWiremockTest {
  private static WireMockServer wireMockServer;
  private static TestClient testClient;
  private static TestSetClient testSetClient;
  private static TestRunClient testRunClient;
  private static FileClient fileClient;

  @BeforeAll
  static void setUp() {
    wireMockServer = new WireMockServer(8089);
    wireMockServer.start();
    WireMock.configureFor("localhost", 8089);

    RhesisClient rhesisClient =
        RhesisClient.builder().baseUrl("http://localhost:8089").apiKey("test-key").build();
    testClient = rhesisClient.tests();
    testSetClient = rhesisClient.testSets();
    testRunClient = rhesisClient.testRuns();
    fileClient = rhesisClient.files();
  }

  @AfterAll
  static void tearDown() {
    wireMockServer.stop();
  }

  @Test
  void testGetTest() {
    stubFor(
        get(urlEqualTo("/tests/t-123"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"id\":\"t-123\",\"test_type\":\"Single-Turn\",\"behavior\":\"b1\"}")));

    com.rhesis.sdk.entities.Test response = testClient.get("t-123");
    assertThat(response.id()).isEqualTo("t-123");
    assertThat(response.testType()).isEqualTo(TestType.SINGLE_TURN);
    assertThat(response.behavior()).isEqualTo("b1");
  }

  @Test
  void testGetTestSet() {
    stubFor(
        get(urlEqualTo("/test_sets/ts-123"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"id\":\"ts-123\",\"name\":\"My TestSet\",\"test_set_type\":\"Multi-Turn\"}")));

    TestSet response = testSetClient.get("ts-123");
    assertThat(response.id()).isEqualTo("ts-123");
    assertThat(response.name()).isEqualTo("My TestSet");
    assertThat(response.testSetType()).isEqualTo(TestType.MULTI_TURN);
  }

  @Test
  void testGetTestRun() {
    stubFor(
        get(urlEqualTo("/test_runs/tr-1"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":\"tr-1\",\"status\":\"Completed\",\"name\":\"Run 1\"}")));

    TestRun response = testRunClient.get("tr-1");
    assertThat(response.id()).isEqualTo("tr-1");
    assertThat(response.status()).isEqualTo(RunStatus.COMPLETED.getValue());
  }

  @Test
  void testGetTestResultsByRun() {
    stubFor(
        get(urlEqualTo("/test_results/?$filter=test_run_id%20eq%20'tr-1'"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("[{\"id\":\"res-1\",\"test_run_id\":\"tr-1\"}]")));

    List<TestResult> response = testRunClient.getTestResults("tr-1");
    assertThat(response).hasSize(1);
    assertThat(response.get(0).id()).isEqualTo("res-1");
  }

  @Test
  void testGetFile() {
    stubFor(
        get(urlEqualTo("/files/file-123"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":\"file-123\",\"filename\":\"data.json\"}")));

    File response = fileClient.get("file-123");
    assertThat(response.id()).isEqualTo("file-123");
    assertThat(response.filename()).isEqualTo("data.json");
  }

  @Test
  void testDownloadFile() {
    stubFor(
        get(urlEqualTo("/files/file-123/content"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/octet-stream")
                    .withBody("hello world")));

    byte[] response = fileClient.download("file-123");
    assertThat(new String(response, java.nio.charset.StandardCharsets.UTF_8))
        .isEqualTo("hello world");
  }
}
