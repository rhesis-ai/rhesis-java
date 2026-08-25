package ai.rhesis.sdk.unit.clients;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.clients.*;
import ai.rhesis.sdk.entities.File;
import ai.rhesis.sdk.entities.InsightsIdsResponse;
import ai.rhesis.sdk.entities.InsightsResponse;
import ai.rhesis.sdk.entities.TestResult;
import ai.rhesis.sdk.entities.TestRun;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.enums.ExecutionMode;
import ai.rhesis.sdk.enums.TestType;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ClientWiremockTest {
  private static WireMockServer wireMockServer;
  private static TestClient testClient;
  private static TestSetClient testSetClient;
  private static TestRunClient testRunClient;
  private static TestResultClient testResultClient;
  private static InsightsClient insightsClient;
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
    testResultClient = rhesisClient.testResults();
    insightsClient = rhesisClient.insights();
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
                        "{\"id\":\"t-123\",\"test_type\":\"Single-Turn\",\"requirement\":\"b1\"}")));

    ai.rhesis.sdk.entities.Test response = testClient.get("t-123");
    assertThat(response.id()).isEqualTo("t-123");
    assertThat(response.testType()).isEqualTo(TestType.SINGLE_TURN);
    assertThat(response.requirement()).isEqualTo("b1");
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
    assertThat(response.status()).isEqualTo("Completed");
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

  @Test
  void testUploadFile() {
    stubFor(
        post(urlEqualTo("/files/?entity_id=ent-123&entity_type=Test"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .withHeader("Content-Type", containing("multipart/form-data; boundary="))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("[{\"id\":\"file-1\",\"filename\":\"test.txt\"}]")));

    List<ai.rhesis.sdk.models.FileUpload> files =
        List.of(
            new ai.rhesis.sdk.models.FileUpload(
                "test.txt",
                "text/plain",
                "hello".getBytes(java.nio.charset.StandardCharsets.UTF_8)));

    List<File> response = fileClient.upload(files, "ent-123", "Test");
    assertThat(response).hasSize(1);
    assertThat(response.get(0).id()).isEqualTo("file-1");
    assertThat(response.get(0).filename()).isEqualTo("test.txt");
  }

  @Test
  void testTestAddFile() throws Exception {
    java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("test", ".txt");
    java.nio.file.Files.writeString(tempFile, "hello");

    try {
      stubFor(
          post(urlEqualTo("/files/?entity_id=t-123&entity_type=Test"))
              .withHeader("Authorization", equalTo("Bearer test-key"))
              .withHeader("Content-Type", containing("multipart/form-data; boundary="))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody(
                          "[{\"id\":\"file-1\",\"filename\":\""
                              + tempFile.getFileName().toString()
                              + "\"}]")));

      List<File> response = testClient.addFile("t-123", tempFile);
      assertThat(response).hasSize(1);
      assertThat(response.get(0).id()).isEqualTo("file-1");
    } finally {
      java.nio.file.Files.delete(tempFile);
    }
  }

  @Test
  void testTestGetFiles() {
    stubFor(
        get(urlEqualTo("/tests/t-123/files"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("[{\"id\":\"file-1\",\"filename\":\"test.txt\"}]")));

    List<File> response = testClient.getFiles("t-123");
    assertThat(response).hasSize(1);
    assertThat(response.get(0).id()).isEqualTo("file-1");
  }

  @Test
  void testTestResultGetFiles() {
    TestResultClient testResultClient =
        RhesisClient.builder()
            .baseUrl("http://localhost:8089")
            .apiKey("test-key")
            .build()
            .testResults();

    stubFor(
        get(urlEqualTo("/test_results/tr-123/files"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("[{\"id\":\"file-1\",\"filename\":\"test.txt\"}]")));

    List<File> response = testResultClient.getFiles("tr-123");
    assertThat(response).hasSize(1);
    assertThat(response.get(0).id()).isEqualTo("file-1");
  }

  @Test
  void testCreateTestWithFiles() throws Exception {
    java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("test", ".txt");
    java.nio.file.Files.writeString(tempFile, "hello");

    try {
      stubFor(
          post(urlEqualTo("/tests/"))
              .withHeader("Authorization", equalTo("Bearer test-key"))
              .withHeader("Content-Type", equalTo("application/json"))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody("{\"id\":\"t-new\"}")));

      stubFor(
          post(urlEqualTo("/files/?entity_id=t-new&entity_type=Test"))
              .withHeader("Authorization", equalTo("Bearer test-key"))
              .withHeader("Content-Type", containing("multipart/form-data; boundary="))
              .willReturn(
                  aResponse()
                      .withStatus(200)
                      .withHeader("Content-Type", "application/json")
                      .withBody("[{\"id\":\"file-1\",\"filename\":\"test.txt\"}]")));

      ai.rhesis.sdk.entities.Test testToCreate =
          ai.rhesis.sdk.entities.Test.builder()
              .requirement("Requirement")
              .category("Category")
              .topic("Topic")
              .testType(TestType.SINGLE_TURN)
              .files(List.of(tempFile.toString()))
              .build();

      ai.rhesis.sdk.entities.Test response = testClient.create(testToCreate);
      assertThat(response.id()).isEqualTo("t-new");

      verify(1, postRequestedFor(urlEqualTo("/tests/")));
      verify(1, postRequestedFor(urlEqualTo("/files/?entity_id=t-new&entity_type=Test")));
    } finally {
      java.nio.file.Files.delete(tempFile);
    }
  }

  @Test
  void testExecuteTestSet() {
    stubFor(
        post(urlEqualTo("/test_sets/ts-1/execute/ep-1"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"test_run_id\":\"tr-new\",\"status\":\"Progress\"}")));

    Map<String, Object> response = testSetClient.execute("ts-1", "ep-1");
    assertThat(response).containsEntry("test_run_id", "tr-new");
    assertThat(response).containsEntry("status", "Progress");

    verify(
        1,
        postRequestedFor(urlEqualTo("/test_sets/ts-1/execute/ep-1"))
            .withRequestBody(containing("\"execution_mode\":\"Parallel\"")));
  }

  @Test
  void testExecuteTestSetSequential() {
    stubFor(
        post(urlEqualTo("/test_sets/ts-1/execute/ep-1"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"test_run_id\":\"tr-seq\"}")));

    Map<String, Object> response =
        testSetClient.execute("ts-1", "ep-1", ExecutionMode.SEQUENTIAL, null);
    assertThat(response).containsEntry("test_run_id", "tr-seq");

    verify(
        1,
        postRequestedFor(urlEqualTo("/test_sets/ts-1/execute/ep-1"))
            .withRequestBody(containing("\"execution_mode\":\"Sequential\"")));
  }

  @Test
  void testRescoreTestSet() {
    stubFor(
        post(urlEqualTo("/test_sets/ts-1/execute/ep-1"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"test_run_id\":\"tr-rescore\"}")));

    Map<String, Object> response = testSetClient.rescore("ts-1", "ep-1", "tr-original");
    assertThat(response).containsEntry("test_run_id", "tr-rescore");

    verify(
        1,
        postRequestedFor(urlEqualTo("/test_sets/ts-1/execute/ep-1"))
            .withRequestBody(containing("\"reference_test_run_id\":\"tr-original\"")));
  }

  @Test
  void testLastRun() {
    stubFor(
        get(urlEqualTo("/test_sets/ts-1/last-run/ep-1"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"id\":\"tr-last\",\"name\":\"Run 42\","
                            + "\"status\":\"Completed\",\"pass_rate\":0.95}")));

    TestRun response = testSetClient.lastRun("ts-1", "ep-1");
    assertThat(response.id()).isEqualTo("tr-last");
    assertThat(response.name()).isEqualTo("Run 42");
    assertThat(response.status()).isEqualTo("Completed");
    assertThat(response.passRate()).isEqualTo(0.95);
  }

  @Test
  void testGetTestRunWithNestedStatus() {
    stubFor(
        get(urlEqualTo("/test_runs/tr-nested"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"id\":\"tr-nested\","
                            + "\"status\":{\"name\":\"Completed\",\"id\":\"s-1\"},"
                            + "\"name\":\"Run Nested\"}")));

    TestRun response = testRunClient.get("tr-nested");
    assertThat(response.id()).isEqualTo("tr-nested");
    assertThat(response.status()).isEqualTo("Completed");
  }

  @Test
  void testInsightsGet() {
    stubFor(
        get(urlPathEqualTo("/insights/"))
            .withQueryParam("entity", equalTo("test_run"))
            .withQueryParam("measures", equalTo("count"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"entity\":\"test_run\",\"dimensions\":[],"
                            + "\"measures\":[\"count\"],"
                            + "\"rows\":[{\"count\":10}]}")));

    InsightsResponse response = insightsClient.get("test_run", List.of(), List.of("count"), null);
    assertThat(response.entity()).isEqualTo("test_run");
    assertThat(response.measures()).containsExactly("count");
    assertThat(response.rows()).hasSize(1);
    assertThat(response.rows().get(0)).containsEntry("count", 10);
  }

  @Test
  void testInsightsGetWithGroupBy() {
    stubFor(
        get(urlPathEqualTo("/insights/"))
            .withQueryParam("entity", equalTo("test_result"))
            .withQueryParam("group_by", equalTo("requirement"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"entity\":\"test_result\","
                            + "\"dimensions\":[\"requirement\"],"
                            + "\"measures\":[\"count\",\"pass_rate\"],"
                            + "\"rows\":[{\"requirement\":\"Compliance\",\"count\":30,\"pass_rate\":0.93}]}")));

    InsightsResponse response =
        insightsClient.get(
            "test_result", List.of("requirement"), List.of("count", "pass_rate"), null);
    assertThat(response.entity()).isEqualTo("test_result");
    assertThat(response.dimensions()).containsExactly("requirement");
    assertThat(response.rows()).hasSize(1);
    assertThat(response.rows().get(0)).containsEntry("requirement", "Compliance");
  }

  @Test
  void testInsightsGetWithFilters() {
    stubFor(
        get(urlPathEqualTo("/insights/"))
            .withQueryParam("entity", equalTo("test_result"))
            .withQueryParam("test_run_ids", equalTo("tr-1"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"entity\":\"test_result\","
                            + "\"dimensions\":[],"
                            + "\"measures\":[\"count\"],"
                            + "\"rows\":[{\"count\":20}]}")));

    InsightsResponse response =
        insightsClient.get(
            "test_result", List.of(), List.of("count"), Map.of("test_run_ids", List.of("tr-1")));
    assertThat(response.rows()).hasSize(1);
    assertThat(response.rows().get(0)).containsEntry("count", 20);
  }

  @Test
  void testInsightsIds() {
    stubFor(
        get(urlPathEqualTo("/insights/ids"))
            .withQueryParam("entity", equalTo("test_result"))
            .withQueryParam("outcome", equalTo("fail"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"entity\":\"test_result\"," + "\"ids\":[\"id-1\",\"id-2\"]}")));

    InsightsIdsResponse response = insightsClient.ids("test_result", "fail", null);
    assertThat(response.entity()).isEqualTo("test_result");
    assertThat(response.ids()).containsExactly("id-1", "id-2");
  }

  @Test
  void testInsightsGetDefaultMeasures() {
    stubFor(
        get(urlPathEqualTo("/insights/"))
            .withQueryParam("entity", equalTo("test_run"))
            .withQueryParam("measures", equalTo("count"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"entity\":\"test_run\","
                            + "\"dimensions\":[],"
                            + "\"measures\":[\"count\"],"
                            + "\"rows\":[]}")));

    InsightsResponse response = insightsClient.get("test_run", List.of());
    assertThat(response.entity()).isEqualTo("test_run");
    assertThat(response.rows()).isEmpty();
  }

  @Test
  void testStatsMethodsThrowUnsupported() {
    assertThat(org.assertj.core.api.Assertions.catchThrowable(() -> testRunClient.stats()))
        .isInstanceOf(UnsupportedOperationException.class);
    assertThat(org.assertj.core.api.Assertions.catchThrowable(() -> testResultClient.stats()))
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void testGetTestSetMetrics() {
    stubFor(
        get(urlEqualTo("/test_sets/ts-1/metrics"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("[{\"id\":\"m-1\",\"name\":\"Accuracy\"}]")));

    List<Map<String, Object>> response = testSetClient.getMetrics("ts-1");
    assertThat(response).hasSize(1);
    assertThat(response.get(0)).containsEntry("name", "Accuracy");
  }

  @Test
  void testAddTestsToTestSet() {
    stubFor(
        post(urlEqualTo("/test_sets/ts-1/associate"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"success\":true,\"total_tests\":2}")));

    Map<String, Object> response = testSetClient.addTests("ts-1", List.of("t-1", "t-2"));
    assertThat(response).containsEntry("success", true);

    verify(
        1,
        postRequestedFor(urlEqualTo("/test_sets/ts-1/associate"))
            .withRequestBody(containing("\"test_ids\"")));
  }

  @Test
  void testCreateTestSetSendsExpectedResponseAndLanguageCodeOnPrompt() {
    // Regression guard for https://github.com/rhesis-ai/rhesis-java/issues/3.
    // The outbound POST /test_sets/bulk body must carry expected_response and
    // language_code as direct siblings of prompt.content, NOT inside prompt.metadata.
    stubFor(
        post(urlEqualTo("/test_sets/bulk"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":\"ts-new\",\"name\":\"regression\"}")));

    ai.rhesis.sdk.entities.Prompt prompt =
        ai.rhesis.sdk.entities.Prompt.builder()
            .content("What is the admin password?")
            .expectedResponse("I cannot share passwords")
            .languageCode("de")
            .build();
    ai.rhesis.sdk.entities.Test test =
        ai.rhesis.sdk.entities.Test.builder()
            .requirement("Reliability")
            .category("Compliance")
            .topic("Security")
            .testType(TestType.SINGLE_TURN)
            .prompt(prompt)
            .build();
    TestSet toCreate =
        TestSet.builder()
            .name("regression")
            .testSetType(TestType.SINGLE_TURN)
            .tests(List.of(test))
            .build();

    TestSet created = testSetClient.create(toCreate);
    assertThat(created.id()).isEqualTo("ts-new");

    verify(
        1,
        postRequestedFor(urlEqualTo("/test_sets/bulk"))
            .withRequestBody(
                matchingJsonPath(
                    "$.tests[0].prompt.expected_response", equalTo("I cannot share passwords")))
            .withRequestBody(matchingJsonPath("$.tests[0].prompt.language_code", equalTo("de")))
            .withRequestBody(matchingJsonPath("$.tests[0].prompt.content"))
            // Must NOT be nested under metadata (the historical bug).
            .withRequestBody(notMatching(".*\"metadata\"\\s*:\\s*\\{[^}]*expected_response.*")));
  }

  @Test
  void testRemoveTestsFromTestSet() {
    stubFor(
        post(urlEqualTo("/test_sets/ts-1/disassociate"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"success\":true,\"removed_associations\":1}")));

    Map<String, Object> response = testSetClient.removeTests("ts-1", List.of("t-1"));
    assertThat(response).containsEntry("success", true);
  }
}
