package ai.rhesis.sdk.synthesizers;

import java.util.ArrayList;
import java.util.List;

public class GenerationConfig {
  private final String generationPrompt;
  private final List<String> behaviors;
  private final List<String> categories;
  private final List<String> topics;
  private final String additionalContext;

  private GenerationConfig(Builder builder) {
    this.generationPrompt = builder.generationPrompt;
    this.behaviors = builder.behaviors != null ? builder.behaviors : new ArrayList<>();
    this.categories = builder.categories != null ? builder.categories : new ArrayList<>();
    this.topics = builder.topics != null ? builder.topics : new ArrayList<>();
    this.additionalContext = builder.additionalContext;
  }

  public String getGenerationPrompt() {
    return generationPrompt;
  }

  public List<String> getBehaviors() {
    return behaviors;
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

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String generationPrompt;
    private List<String> behaviors;
    private List<String> categories;
    private List<String> topics;
    private String additionalContext;

    public Builder generationPrompt(String generationPrompt) {
      this.generationPrompt = generationPrompt;
      return this;
    }

    public Builder behaviors(List<String> behaviors) {
      this.behaviors = behaviors;
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

    public GenerationConfig build() {
      if (generationPrompt == null) {
        throw new IllegalArgumentException("generationPrompt cannot be null");
      }
      return new GenerationConfig(this);
    }
  }
}
