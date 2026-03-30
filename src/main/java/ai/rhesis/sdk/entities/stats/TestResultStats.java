package ai.rhesis.sdk.entities.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record TestResultStats(
    @JsonProperty("metric_pass_rates") Map<String, MetricStats> metricPassRates,
    @JsonProperty("behavior_pass_rates") Map<String, MetricStats> behaviorPassRates,
    @JsonProperty("category_pass_rates") Map<String, MetricStats> categoryPassRates,
    @JsonProperty("topic_pass_rates") Map<String, MetricStats> topicPassRates,
    @JsonProperty("overall_pass_rates") OverallStats overallPassRates,
    @JsonProperty("timeline") List<TimelineData> timeline,
    @JsonProperty("test_run_summary") List<TestRunSummary> testRunSummary,
    @JsonProperty("metadata") TestResultStatsMetadata metadata) {}
