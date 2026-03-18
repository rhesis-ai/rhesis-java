package com.rhesis.sdk.examples;

import com.rhesis.sdk.RhesisClient;
import com.rhesis.sdk.entities.TestSet;
import com.rhesis.sdk.synthesizers.GenerationConfig;
import com.rhesis.sdk.synthesizers.MultiTurnSynthesizer;
import java.util.Arrays;

public class GenerateTestSetExample {
  public static void main(String[] args) {
    // Initialize the global default client (needed for synthesizer)
    RhesisClient client = RhesisClient.builder().apiKey(System.getenv("RHESIS_API_KEY")).build();
    RhesisClient.setDefault(client);

    // Configure the synthesizer
    System.out.println("Initializing MultiTurnSynthesizer...");
    GenerationConfig config =
        GenerationConfig.builder()
            .generationPrompt(
                "You are a helpful travel assistant. You must never provide medical advice.")
            .behaviors(Arrays.asList("Refuses medical advice", "Provides travel itineraries"))
            .categories(Arrays.asList("Safety", "Functionality"))
            .topics(Arrays.asList("Medical", "Travel"))
            .build();

    MultiTurnSynthesizer synthesizer = new MultiTurnSynthesizer(config, 10);

    // Generate the tests
    System.out.println("Generating test set (this may take a moment)...");
    TestSet generatedTestSet = synthesizer.generate(5);

    System.out.println("Generated TestSet locally with name: " + generatedTestSet.name());
    System.out.println("Number of tests generated: " + generatedTestSet.tests().size());

    // Push the test set to the Rhesis platform
    System.out.println("Pushing test set to Rhesis...");
    TestSet pushedTestSet = client.testSets().create(generatedTestSet);

    System.out.println("Successfully pushed TestSet! ID: " + pushedTestSet.id());
  }
}
