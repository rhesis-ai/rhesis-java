package ai.rhesis.sdk.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import ai.rhesis.sdk.entities.File;
import ai.rhesis.sdk.enums.TestType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FileIntegrationTest extends BaseIntegrationTest {

  private static ai.rhesis.sdk.entities.Test createdTest;
  private static Path tempFile;

  @BeforeAll
  static void setUpFiles() throws Exception {
    assumeTrue(
        System.getenv("RHESIS_API_KEY") != null || System.getProperty("RHESIS_API_KEY") != null,
        "Skipping integration tests because API key is not set");

    // Create a dummy image file (1x1 transparent PNG)
    tempFile = Files.createTempFile("rhesis-integration-test", ".png");
    byte[] pngData =
        new byte[] {
          (byte) 0x89,
          0x50,
          0x4e,
          0x47,
          0x0d,
          0x0a,
          0x1a,
          0x0a,
          0x00,
          0x00,
          0x00,
          0x0d,
          0x49,
          0x48,
          0x44,
          0x52,
          0x00,
          0x00,
          0x00,
          0x01,
          0x00,
          0x00,
          0x00,
          0x01,
          0x08,
          0x06,
          0x00,
          0x00,
          0x00,
          0x1f,
          0x15,
          (byte) 0xc4,
          (byte) 0x89,
          0x00,
          0x00,
          0x00,
          0x0d,
          0x49,
          0x44,
          0x41,
          0x54,
          0x78,
          (byte) 0xda,
          0x63,
          0x60,
          0x00,
          0x00,
          0x00,
          0x02,
          0x00,
          0x01,
          (byte) 0xe5,
          0x27,
          (byte) 0xde,
          (byte) 0xfc,
          0x00,
          0x00,
          0x00,
          0x00,
          0x49,
          0x45,
          0x4e,
          0x44,
          (byte) 0xae,
          0x42,
          0x60,
          (byte) 0x82
        };
    Files.write(tempFile, pngData);

    // Create a test to attach files to
    ai.rhesis.sdk.entities.Test testToCreate =
        ai.rhesis.sdk.entities.Test.builder()
            .behavior("Integration Test Behavior")
            .category("SDK")
            .topic("Files Integration")
            .testType(TestType.SINGLE_TURN)
            .metadata(Map.of())
            .files(List.of(tempFile.toString()))
            .build();

    createdTest = client.tests().create(testToCreate);
  }

  @AfterAll
  static void tearDownFiles() throws Exception {
    if (createdTest != null) {
      try {
        client.tests().delete(createdTest.id());
      } catch (Exception e) {
        System.err.println("Failed to cleanup test: " + e.getMessage());
      }
    }
    if (tempFile != null) {
      Files.deleteIfExists(tempFile);
    }
  }

  @Test
  void testFileLifecycle() {
    // 1. Upload additional file (reusing temp file for simplicity)
    List<File> uploadedFiles = client.tests().addFile(createdTest.id(), tempFile);

    assertThat(uploadedFiles).hasSize(1);
    File uploadedFile = uploadedFiles.get(0);
    assertThat(uploadedFile.id()).isNotNull();
    assertThat(uploadedFile.filename()).isEqualTo(tempFile.getFileName().toString());

    // 2. List files
    List<File> listedFiles = client.tests().getFiles(createdTest.id());
    assertThat(listedFiles).hasSizeGreaterThanOrEqualTo(1);
    assertThat(listedFiles.stream().map(File::id)).contains(uploadedFile.id());

    // 3. Download file
    byte[] downloadedContent = client.files().download(uploadedFile.id());
    assertThat(downloadedContent).isNotEmpty();

    // 4. Delete file
    client.files().delete(uploadedFile.id());

    // Verify deletion
    List<File> filesAfterDelete = client.tests().getFiles(createdTest.id());
    assertThat(filesAfterDelete.stream().map(File::id)).doesNotContain(uploadedFile.id());
  }

  @Test
  void testFileUploadFromBase64() throws Exception {
    // 1. Convert our valid PNG bytes into a base64 string
    byte[] pngData = Files.readAllBytes(tempFile);
    String base64Data = java.util.Base64.getEncoder().encodeToString(pngData);

    // 2. Test uploading it directly using the base64 convenience method
    List<File> uploadedFiles =
        client
            .tests()
            .addFileFromBase64(createdTest.id(), "base64_test.png", "image/png", base64Data);

    assertThat(uploadedFiles).hasSize(1);
    File uploadedFile = uploadedFiles.get(0);
    assertThat(uploadedFile.id()).isNotNull();
    assertThat(uploadedFile.filename()).isEqualTo("base64_test.png");

    // 5. Clean up the explicit base64 file upload
    client.files().delete(uploadedFile.id());
  }
}
