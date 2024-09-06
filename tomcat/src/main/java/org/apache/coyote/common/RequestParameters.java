package org.apache.coyote.common;

import java.util.Map;

public class RequestParameters {

    private final Map<String, String> values;

    public RequestParameters(Map<String, String> values) {
        this.values = values;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public String getValue(String key) {
        return values.get(key);
    }

    @Override
    public String toString() {
        return "RequestParameters{" +
               "values=" + values +
               '}';
    }
}
