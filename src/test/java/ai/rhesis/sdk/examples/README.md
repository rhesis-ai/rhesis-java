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
| `TestRunWorkflowExample` | Full lifecycle: list runs, inspect results, fetch stats, get last run, and rescore. |

### Test Set Management

| Example | Description |
|---------|-------------|
| `TestSetMetricsExample` | List, add, and remove metrics on a test set. Associate and disassociate tests. |

### Analytics & Stats

| Example | Description |
|---------|-------------|
| `TestRunStatsExample` | Test run analytics: overall summary, status distribution, most-run test sets, timeline, filtering by mode/months/run IDs. |
| `TestResultStatsExample` | Test result analytics: pass rates by metric, behavior, category, and topic. Timeline trends, per-run summaries, and filtered queries. |

## Quick Reference

```java
// Initialize the client
RhesisClient client = RhesisClient.builder()
    .apiKey(System.getenv("RHESIS_API_KEY"))
    .build();

// Execute a test set
Map<String, Object> result = client.testSets()
    .execute(testSetId, endpointId);

// Get test run stats
TestRunStats stats = client.testRuns().stats();
System.out.println("Pass rate: " + stats.overallSummary().passRate() + "%");

// Get test result stats by behavior
TestResultStats behaviorStats = client.testResults()
    .stats(TestResultStatsMode.BEHAVIOR);

// Get last completed run
TestRun lastRun = client.testSets()
    .lastRun(testSetId, endpointId);

// Rescore a previous run
Map<String, Object> rescore = client.testSets()
    .rescore(testSetId, endpointId, lastRun.id());
```
