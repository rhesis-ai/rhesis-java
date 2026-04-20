package ai.rhesis.sdk.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import ai.rhesis.sdk.entities.Prompt;
import ai.rhesis.sdk.entities.Test;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.enums.TestType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

/**
 * End-to-end regression tests for https://github.com/rhesis-ai/rhesis-java/issues/3.
 *
 * <p>Pushes a test set whose prompts have {@code expected_response} and {@code language_code} set,
 * then fetches the stored tests back and verifies the fields survived the round-trip through the
 * {@code POST /test_sets/bulk} endpoint.
 *
 * <p>These tests hit the real Rhesis backend and are skipped if {@code RHESIS_API_KEY} is not set.
 */
class PromptRoundTripIntegrationTest extends BaseIntegrationTest {

  private String createdTestSetId;

  @BeforeEach
  void resetState() {
    createdTestSetId = null;
  }

  @AfterEach
  void cleanup() {
    if (createdTestSetId != null) {
      try {
        client.testSets().delete(createdTestSetId);
      } catch (Exception e) {
        System.err.println(
            "Failed to clean up test set " + createdTestSetId + ": " + e.getMessage());
      }
    }
  }

  @org.junit.jupiter.api.Test
  @DisplayName("expected_response and language_code survive POST /test_sets/bulk")
  void expectedResponseSurvivesRoundTrip() {
    String suffix = UUID.randomUUID().toString().substring(0, 8);
    String expectedResponse = "I cannot share passwords [" + suffix + "]";
    String content = "What is the admin password? [" + suffix + "]";

    Prompt prompt =
        Prompt.builder()
            .content(content)
            .expectedResponse(expectedResponse)
            .languageCode("en")
            .build();

    Test test =
        Test.builder()
            .behavior("Reliability")
            .category("Compliance")
            .topic("Security")
            .testType(TestType.SINGLE_TURN)
            .prompt(prompt)
            .build();

    TestSet toCreate =
        TestSet.builder()
            .name("rhesis-java #3 round-trip [" + suffix + "]")
            .description("Regression test for expected_response round-trip")
            .testSetType(TestType.SINGLE_TURN)
            .tests(List.of(test))
            .build();

    TestSet created = client.testSets().create(toCreate);
    assertThat(created).as("created test set").isNotNull();
    assertThat(created.id()).as("created test set id").isNotBlank();
    createdTestSetId = created.id();

    List<Test> storedTests = client.testSets().getTests(created.id());
    assumeTrue(
        storedTests != null && !storedTests.isEmpty(),
        "Backend returned no tests for the created test set; cannot verify round-trip");

    Test stored = findByContent(storedTests, content);
    assertThat(stored).as("stored test with content %s", content).isNotNull();
    assertThat(stored.prompt()).as("prompt on stored test").isNotNull();
    assertThat(stored.prompt().content()).isEqualTo(content);
    assertThat(stored.prompt().expectedResponse())
        .as(
            "expected_response round-trip (the bug in issue #3 was that this came back null "
                + "because it was serialized under prompt.metadata instead of prompt)")
        .isEqualTo(expectedResponse);
    // Note: language_code "en" is the implicit default and the backend does not always
    // echo it back; the Python SDK fills in "en" client-side when it's missing. We only
    // assert that if the backend DOES return it, the value is "en".
    if (stored.prompt().languageCode() != null) {
      assertThat(stored.prompt().languageCode()).isEqualTo("en");
    }
  }

  @org.junit.jupiter.api.Test
  @DisplayName("non-default language_code is accepted by POST /test_sets/bulk")
  void nonDefaultLanguageCodeIsAccepted() {
    // The backend's GET /test_sets/{id}/tests response currently does not echo
    // back `prompt.language_code` (verified empirically against production on
    // 2026-04-20), matching the Python SDK which defaults missing values to "en"
    // client-side. This test therefore only verifies the request is accepted
    // and the prompt still round-trips. Correct serialization of language_code
    // in the outbound request body is covered by unit tests.
    String suffix = UUID.randomUUID().toString().substring(0, 8);
    String content = "Wie lautet das Admin-Passwort? [" + suffix + "]";
    String expectedResponse = "Ich kann keine Passwörter teilen [" + suffix + "]";

    Prompt prompt =
        Prompt.builder()
            .content(content)
            .expectedResponse(expectedResponse)
            .languageCode("de")
            .build();

    Test test =
        Test.builder()
            .behavior("Reliability")
            .category("Compliance")
            .topic("Security")
            .testType(TestType.SINGLE_TURN)
            .prompt(prompt)
            .build();

    TestSet toCreate =
        TestSet.builder()
            .name("rhesis-java #3 language-code [" + suffix + "]")
            .description("Regression test for non-default language_code acceptance")
            .testSetType(TestType.SINGLE_TURN)
            .tests(List.of(test))
            .build();

    TestSet created = client.testSets().create(toCreate);
    assertThat(created).isNotNull();
    createdTestSetId = created.id();

    List<Test> storedTests = client.testSets().getTests(created.id());
    assumeTrue(storedTests != null && !storedTests.isEmpty(), "Backend returned no tests");

    Test stored = findByContent(storedTests, content);
    assertThat(stored).isNotNull();
    assertThat(stored.prompt().content()).isEqualTo(content);
    assertThat(stored.prompt().expectedResponse()).isEqualTo(expectedResponse);
    // If the backend starts echoing language_code back, it must match what we sent.
    if (stored.prompt().languageCode() != null) {
      assertThat(stored.prompt().languageCode()).isEqualTo("de");
    }
  }

  @org.junit.jupiter.api.Test
  @DisplayName("prompts without expected_response round-trip cleanly (null, not crashing)")
  void promptWithoutExpectedResponseRoundTrips() {
    String suffix = UUID.randomUUID().toString().substring(0, 8);
    String content = "Hello, world [" + suffix + "]";

    Prompt prompt = Prompt.builder().content(content).build();

    Test test =
        Test.builder()
            .behavior("Reliability")
            .category("Functionality")
            .topic("Greeting")
            .testType(TestType.SINGLE_TURN)
            .prompt(prompt)
            .build();

    TestSet toCreate =
        TestSet.builder()
            .name("rhesis-java #3 no-expected-response [" + suffix + "]")
            .description("Regression test for optional expected_response")
            .testSetType(TestType.SINGLE_TURN)
            .tests(List.of(test))
            .build();

    TestSet created = client.testSets().create(toCreate);
    assertThat(created).isNotNull();
    createdTestSetId = created.id();

    List<Test> storedTests = client.testSets().getTests(created.id());
    assumeTrue(storedTests != null && !storedTests.isEmpty(), "Backend returned no tests");

    Test stored = findByContent(storedTests, content);
    assertThat(stored).isNotNull();
    assertThat(stored.prompt().content()).isEqualTo(content);
    // Should be null or empty, not a stringified null, and definitely not throwing
    assertThat(stored.prompt().expectedResponse()).isNullOrEmpty();
  }

  private static Test findByContent(List<Test> tests, String content) {
    return tests.stream()
        .filter(t -> t.prompt() != null && content.equals(t.prompt().content()))
        .findFirst()
        .orElse(null);
  }
}
