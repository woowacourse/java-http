package org.apache.coyote.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestParameters {

    private static final String QUERY_STRING_DELIMITER = "&";
    private static final String QUERY_FIELD_DELIMITER = "=";
    private static final int SPLIT_LIMIT = 2;

    private final Map<String, String> values;

    public RequestParameters(String queryString) {
        this.values = parseFields(queryString);
    }

    public RequestParameters(Map<String, String> values) {
        this.values = values;
    }

    public static RequestParameters empty() {
        return new RequestParameters(Map.of());
    }

    private static Map<String, String> parseFields(String line) {
        String[] parameters = line.split(QUERY_STRING_DELIMITER);
        return Arrays.stream(parameters)
                .map(param -> param.split(QUERY_FIELD_DELIMITER, SPLIT_LIMIT))
                .filter(param -> param.length == SPLIT_LIMIT)
                .collect(HashMap::new,
                         (map, entry) -> map.put(entry[0], entry[1]),
                         HashMap::putAll);
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
