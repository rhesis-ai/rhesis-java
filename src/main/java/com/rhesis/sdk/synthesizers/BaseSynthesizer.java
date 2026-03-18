package com.rhesis.sdk.synthesizers;

import com.hubspot.jinjava.Jinjava;
import com.rhesis.sdk.entities.TestSet;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class BaseSynthesizer {
  protected final Jinjava jinjava;
  protected final int batchSize;

  public BaseSynthesizer(int batchSize) {
    this.jinjava = new Jinjava();
    this.batchSize = batchSize;
  }

  protected String renderTemplate(String templateName, Map<String, Object> context) {
    String templatePath = "/templates/" + templateName;
    try (InputStream is = getClass().getResourceAsStream(templatePath)) {
      if (is == null) {
        throw new IllegalArgumentException("Template not found: " + templatePath);
      }
      String template = new String(is.readAllBytes(), StandardCharsets.UTF_8);
      return jinjava.render(template, context);
    } catch (Exception e) {
      throw new RuntimeException("Failed to render template", e);
    }
  }

  public abstract TestSet generate(int numTests);
}
