package ai.rhesis.sdk.examples;

import ai.rhesis.sdk.RhesisClient;
import ai.rhesis.sdk.entities.Test;
import ai.rhesis.sdk.entities.TestSet;
import ai.rhesis.sdk.synthesizers.GenerationConfig;
import ai.rhesis.sdk.synthesizers.MultiTurnSynthesizer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class GenerateTestSetWithFilesExample {
  public static void main(String[] args) throws Exception {
    // Initialize the global default client
    RhesisClient client = RhesisClient.builder().apiKey(System.getenv("RHESIS_API_KEY")).build();
    RhesisClient.setDefault(client);

    // Configure the synthesizer
    System.out.println("Initializing MultiTurnSynthesizer...");
    GenerationConfig config =
        GenerationConfig.builder()
            .generationPrompt(
                "You are testing an HR document processor. Generate tests involving reading policy documents.")
            .behaviors(Arrays.asList("Accurately summarizes policies", "Identifies vacation days"))
            .categories(Arrays.asList("Functionality", "Document Processing"))
            .topics(Arrays.asList("HR", "Time Off"))
            .build();

    MultiTurnSynthesizer synthesizer = new MultiTurnSynthesizer(config, 5);

    // Generate the tests
    System.out.println("Generating test set (this may take a moment)...");
    TestSet generatedTestSet = synthesizer.generate(2);

    System.out.println("Generated TestSet locally with name: " + generatedTestSet.name());
    System.out.println("Number of tests generated: " + generatedTestSet.tests().size());

    // Push the test set to the Rhesis platform
    System.out.println("Pushing test set to Rhesis...");
    TestSet pushedTestSet = client.testSets().create(generatedTestSet);
    System.out.println("Successfully pushed TestSet! ID: " + pushedTestSet.id());

    // Create a dummy file to attach to each test
    Path tempFile = Files.createTempFile("company_policy", ".txt");
    Files.writeString(
        tempFile, "Company Policy: Employees are entitled to 20 vacation days per year.");
    System.out.println("Attaching file to each generated test...");
    // Retrieve the tests that were actually created on the backend
    List<Test> remoteTests = client.testSets().getTests(pushedTestSet.id());
    System.out.println("Fetched " + remoteTests.size() + " tests from the backend.");

    for (Test test : remoteTests) {
      if (test.id() != null) {
        System.out.println("Attaching file to test: " + test.id());
        client.tests().addFile(test.id(), tempFile);
      }
    }

    System.out.println("Successfully attached files!");

    // Clean up our local temporary file
    Files.deleteIfExists(tempFile);
  }
}
