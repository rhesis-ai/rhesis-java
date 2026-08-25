# Rhesis Java SDK Examples

Runnable examples demonstrating the core capabilities of the Rhesis Java SDK.

## Prerequisites

- Java 21+
- Maven 3.8+
- A Rhesis API key (get one at [rhesis.ai](https://rhesis.ai))

## Setup

### 1. Install the SDK

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>ai.rhesis</groupId>
    <artifactId>rhesis-java</artifactId>
    <version>0.1.1</version>
</dependency>
```

Or build from source:

```bash
git clone https://github.com/rhesis-ai/rhesis-java.git
cd rhesis-java
mvn install -DskipTests
```

### 2. Set your API key

Create a `.env` file in the project root:

```
RHESIS_API_KEY=your-api-key-here
```

Or export it as an environment variable:

```bash
export RHESIS_API_KEY=your-api-key-here
```

### 3. Run an example

```bash
mvn compile test-compile exec:java \
  -Dexec.mainClass="ai.rhesis.sdk.examples.TestRunStatsExample" \
  -Dexec.classpathScope=test
```

Replace the class name with any example listed below.

## Examples

### Endpoints & Projects

| Example | Description |
|---------|-------------|
| `CreateEndpointExample` | Look up a project by name and create a REST endpoint with request/response mappings. |

### Test Generation

| Example | Description |
|---------|-------------|
| `GenerateTestSetExample` | Configure a multi-turn synthesizer, generate tests, and push the test set to Rhesis. |
| `GenerateTestSetWithFilesExample` | Generate a test set, push it, then attach files to each test. |

### File Management

| Example | Description |
|---------|-------------|
| `FileSupportExample` | Create a test with an attached file, list files, download content, then clean up. |

### Test Execution

| Example | Description |
|---------|-------------|
| `ExecuteTestSetExample` | Trigger a test set run against an endpoint — parallel mode, sequential mode, and with custom metrics. |
| `TestRunWorkflowExample` | Full lifecycle: list runs, inspect results, get insights, get last run, and rescore. |

### Test Set Management

| Example | Description |
|---------|-------------|
| `TestSetMetricsExample` | List, add, and remove metrics on a test set. Associate and disassociate tests. |

### Insights

| Example | Description |
|---------|-------------|
| `TestRunStatsExample` | Test run insights: counts by status, date range filtering with months and start/end dates. |
| `TestResultStatsExample` | Test result insights: pass rates by requirement, category, and topic. Date range queries and failed test result ID retrieval. |

## Quick Reference

```java
// Initialize the client
RhesisClient client = RhesisClient.builder()
    .apiKey(System.getenv("RHESIS_API_KEY"))
    .build();

// Generate a named test set
GenerationConfig config = GenerationConfig.builder()
    .generationPrompt("Test a customer support chatbot")
    .testSetName("Support Bot Safety Tests")
    .testSetDescription("Adversarial tests for the support chatbot")
    .requirements(List.of("Refuses harmful requests", "Stays on topic"))
    .build();
TestSet testSet = new MultiTurnSynthesizer(config).generate(10);
client.testSets().create(testSet);

// Rename an existing test set
TestSet existing = client.testSets().get(testSetId);
TestSet renamed = existing.toBuilder().name("New Name").build();
client.testSets().update(renamed);

// Execute a test set
Map<String, Object> result = client.testSets()
    .execute(testSetId, endpointId);

// Get insights (replaces stats)
InsightsResponse insights = client.insights()
    .get("test_result", List.of("requirement"), List.of("count", "pass_rate"), null);

// Get insights with date range
InsightsQuery query = InsightsQuery.builder("test_result")
    .groupBy(List.of("category"))
    .measures(List.of("count"))
    .months(6)
    .build();
InsightsResponse recent = client.insights().get(query);

// Get last completed run
TestRun lastRun = client.testSets()
    .lastRun(testSetId, endpointId);

// Rescore a previous run
Map<String, Object> rescore = client.testSets()
    .rescore(testSetId, endpointId, lastRun.id());
```
