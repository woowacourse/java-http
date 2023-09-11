package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormUrlEncodedBody implements Body {
    private static final String QUERY_DELIMITER = "&";
    private static final String QUERY_SEPARATOR = "=";
    private final Map<String, String> parameters;

    private FormUrlEncodedBody(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public static Body parse(final String body) {
        final HashMap<String, String> parameters = new HashMap<>();
        final String[] tokens = body.split(QUERY_DELIMITER);
        for (String token : tokens) {
            final List<String> keyValues = List.of(token.split(QUERY_SEPARATOR));
            if (keyValues.size() == 2) {
                parameters.put(keyValues.get(0), keyValues.get(1));
            } else {
                parameters.put(keyValues.get(0), "");
            }
        }
        return new FormUrlEncodedBody(parameters);
    }

    @Override
    public String getValue(String key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key);
        }
        throw new IllegalArgumentException("존재하지 않는 파라미터입니다.");
    }

    @Override
    public String toString() {
        return "HttpRequestBody{" +
                "parameters=" + parameters +
                '}';
    }
}
