package ai.rhesis.sdk.examples;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.File;
import ai.rhesis.sdk.entities.Test;
import ai.rhesis.sdk.enums.TestType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class FileSupportExample {
  public static void main(String[] args) throws Exception {
    RhesisClient client = RhesisClient.builder().apiKey(System.getenv("RHESIS_API_KEY")).build();

    // 1. Create a dummy file
    Path tempFile = Files.createTempFile("rhesis-test", ".txt");
    Files.writeString(tempFile, "Hello, Rhesis!");

    // 2. Create a test
    Test test =
        Test.builder()
            .behavior("File Test Behavior")
            .category("SDK")
            .topic("Files")
            .testType(TestType.SINGLE_TURN)
            .metadata(Map.of())
            .files(List.of(tempFile.toString()))
            .build();

    Test created = client.tests().create(test);
    System.out.println("Created test: " + created.id());

    // 4. List files on test
    List<File> files = client.tests().getFiles(created.id());
    System.out.println("Test has " + files.size() + " files");

    // 5. Download file content
    if (!files.isEmpty()) {
      byte[] content = client.files().download(files.get(0).id());
      System.out.println(
          "Downloaded content: " + new String(content, java.nio.charset.StandardCharsets.UTF_8));
    }

    // Clean up
    for (File f : files) {
      client.files().delete(f.id());
    }
    client.tests().delete(created.id());
    Files.delete(tempFile);
  }
}
