package com.rhesis.sdk.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.rhesis.sdk.entities.TestSet;
import java.util.List;
import org.junit.jupiter.api.Test;

class TestSetIntegrationTest extends BaseIntegrationTest {

  @Test
  void testTestSetList() {
    // Creating TestSets requires type lookups which we don't have yet,
    // so we'll just test that list works.
    List<TestSet> testSets = client.testSets().list();
    assertThat(testSets).isNotNull();
  }
}
