package christopher.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"type", "results"})
public class QueryResults<T> {
    public enum Type {STATION_DATA}
    Type type;
    List<T> results;

    public QueryResults() {
    }

    public QueryResults(Type type, List<T> results) {
        this.type = type;
        this.results = results;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
