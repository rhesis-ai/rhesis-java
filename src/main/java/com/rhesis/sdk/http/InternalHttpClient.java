package com.rhesis.sdk.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rhesis.sdk.exceptions.RhesisApiException;
import com.rhesis.sdk.exceptions.RhesisValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Set;

public class InternalHttpClient {

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final Validator validator;
  private final String baseUrl;
  private final String apiKey;

  public InternalHttpClient(String baseUrl, String apiKey) {
    this.baseUrl = baseUrl;
    this.apiKey = apiKey;

    this.httpClient =
        HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    this.objectMapper =
        new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      this.validator = factory.getValidator();
    }
  }

  public <T, R> R post(String path, T requestBody, Class<R> responseType) {
    validate(requestBody);

    try {
      String jsonBody = objectMapper.writeValueAsString(requestBody);

      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(baseUrl + path))
              .header("Content-Type", "application/json")
              .header("Authorization", "Bearer " + apiKey)
              .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
              .build();

      return executeRequest(request, responseType);

    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize request body", e);
    }
  }

  public <R> R get(String path, Class<R> responseType) {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .header("Authorization", "Bearer " + apiKey)
            .GET()
            .build();

    return executeRequest(request, responseType);
  }

  private <R> R executeRequest(HttpRequest request, Class<R> responseType) {
    try {
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() >= 400) {
        throw new RhesisApiException("API request failed", response.statusCode(), response.body());
      }

      R result = objectMapper.readValue(response.body(), responseType);
      validate(result);
      return result;

    } catch (IOException | InterruptedException e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      throw new RuntimeException("Failed to execute request", e);
    }
  }

  private <T> void validate(T object) {
    if (object == null) return;

    Set<ConstraintViolation<T>> violations = validator.validate(object);
    if (!violations.isEmpty()) {
      throw new RhesisValidationException(violations);
    }
  }
}
