package org.apache.coyote.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParameters {

    private final Map<String, List<String>> values;

    public QueryParameters(Map<String, List<String>> values) {
        this.values = Map.copyOf(values);
    }

    public String getValueBy(String key) {
        validateKey(key);
        return values.get(key).getFirst();
    }

    public List<String> getValuesBy(String key) {
        validateKey(key);
        return values.get(key);
    }

    private void validateKey(String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException(key + "는 존재하지 않는 key 값 입니다.");
        }
    }

    public boolean hasSingleValue(String key) {
        return values.get(key).size() < 2;
    }

    public boolean hasParameters() {
        return !values.isEmpty();
    }

    @Override
    public String toString() {
        return values.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }
}
