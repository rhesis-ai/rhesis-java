package ai.rhesis.sdk.entities;

import java.util.List;
import java.util.Map;

public class InsightsQuery {
  private final String entity;
  private final List<String> groupBy;
  private final List<String> measures;
  private final Map<String, List<String>> filters;
  private final Integer months;
  private final String startDate;
  private final String endDate;

  private InsightsQuery(Builder builder) {
    this.entity = builder.entity;
    this.groupBy = builder.groupBy;
    this.measures = builder.measures;
    this.filters = builder.filters;
    this.months = builder.months;
    this.startDate = builder.startDate;
    this.endDate = builder.endDate;
  }

  public static Builder builder(String entity) {
    return new Builder(entity);
  }

  public String entity() {
    return entity;
  }

  public List<String> groupBy() {
    return groupBy;
  }

  public List<String> measures() {
    return measures;
  }

  public Map<String, List<String>> filters() {
    return filters;
  }

  public Integer months() {
    return months;
  }

  public String startDate() {
    return startDate;
  }

  public String endDate() {
    return endDate;
  }

  public static class Builder {
    private final String entity;
    private List<String> groupBy;
    private List<String> measures;
    private Map<String, List<String>> filters;
    private Integer months;
    private String startDate;
    private String endDate;

    private Builder(String entity) {
      this.entity = entity;
    }

    public Builder groupBy(List<String> groupBy) {
      this.groupBy = groupBy;
      return this;
    }

    public Builder measures(List<String> measures) {
      this.measures = measures;
      return this;
    }

    public Builder filters(Map<String, List<String>> filters) {
      this.filters = filters;
      return this;
    }

    public Builder months(int months) {
      this.months = months;
      return this;
    }

    public Builder startDate(String startDate) {
      this.startDate = startDate;
      return this;
    }

    public Builder endDate(String endDate) {
      this.endDate = endDate;
      return this;
    }

    public InsightsQuery build() {
      return new InsightsQuery(this);
    }
  }
}
