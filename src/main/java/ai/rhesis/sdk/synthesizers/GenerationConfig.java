package ai.rhesis.sdk.synthesizers;

import java.util.ArrayList;
import java.util.List;

public class GenerationConfig {
  private final String generationPrompt;
  private final List<String> requirements;
  private final List<String> categories;
  private final List<String> topics;
  private final String additionalContext;
  private final String testSetName;
  private final String testSetDescription;

  private GenerationConfig(Builder builder) {
    this.generationPrompt = builder.generationPrompt;
    this.requirements = builder.requirements != null ? builder.requirements : new ArrayList<>();
    this.categories = builder.categories != null ? builder.categories : new ArrayList<>();
    this.topics = builder.topics != null ? builder.topics : new ArrayList<>();
    this.additionalContext = builder.additionalContext;
    this.testSetName = builder.testSetName;
    this.testSetDescription = builder.testSetDescription;
  }

  public String getGenerationPrompt() {
    return generationPrompt;
  }

  public List<String> getRequirements() {
    return requirements;
  }

  public List<String> getCategories() {
    return categories;
  }

  public List<String> getTopics() {
    return topics;
  }

  public String getAdditionalContext() {
    return additionalContext;
  }

  public String getTestSetName() {
    return testSetName;
  }

  public String getTestSetDescription() {
    return testSetDescription;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String generationPrompt;
    private List<String> requirements;
    private List<String> categories;
    private List<String> topics;
    private String additionalContext;
    private String testSetName;
    private String testSetDescription;

    public Builder generationPrompt(String generationPrompt) {
      this.generationPrompt = generationPrompt;
      return this;
    }

    public Builder requirements(List<String> requirements) {
      this.requirements = requirements;
      return this;
    }

    public Builder categories(List<String> categories) {
      this.categories = categories;
      return this;
    }

    public Builder topics(List<String> topics) {
      this.topics = topics;
      return this;
    }

    public Builder additionalContext(String additionalContext) {
      this.additionalContext = additionalContext;
      return this;
    }

    public Builder testSetName(String testSetName) {
      this.testSetName = testSetName;
      return this;
    }

    public Builder testSetDescription(String testSetDescription) {
      this.testSetDescription = testSetDescription;
      return this;
    }

    public GenerationConfig build() {
      if (generationPrompt == null) {
        throw new IllegalArgumentException("generationPrompt cannot be null");
      }
      return new GenerationConfig(this);
    }
  }
}
