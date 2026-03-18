package com.rhesis.sdk;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RhesisClientBuilderTest {

  @Test
  void testExplicitApiKey() {
    RhesisClient client =
        RhesisClient.builder().apiKey("explicit-key").baseUrl("http://custom.url").build();

    assertThat(client).isNotNull();
    assertThat(client.models()).isNotNull();
    assertThat(client.synthesizers()).isNotNull();
  }
}
