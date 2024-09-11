package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    private Map<String, String> values;

    public Cookie() {
        this.values = new HashMap<>();
    }

    public Cookie(Map<String, String> values) {
        this.values = new HashMap<>(values);
    }

    public String getValueBy(String key) {
        return values.get(key);
    }

    public void add(String key, String value) {
        values.put(key, value);
    }

    public boolean exist(String key) {
        return values.containsKey(key);
    }

    public String getSessionId() {
        return getValueBy("JSESSIONID");
    }

    public boolean containsSessionId() {
        if (values.containsKey("JSESSIONID")) {
            return true;
        }
        return false;
    }

    public Map<String, String> getValues() {
        return Map.copyOf(values);
    }

    @Override
    public String toString() {
        return values.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }
}
