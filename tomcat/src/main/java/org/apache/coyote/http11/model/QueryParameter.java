package org.apache.coyote.http11.model;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class QueryParameter {

    private final Map<String, String> queryParameters;

    public QueryParameter() {
        this.queryParameters = new HashMap<>();
    }

    public QueryParameter(final String queryString) {
        this.queryParameters = parse(queryString);
    }

    private QueryParameter(final Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public static QueryParameter fromBody(final String body) {
        return new QueryParameter(parse(body));
    }

    private static Map<String, String> parse(String data) {
        final Map<String, String> parameters = new HashMap<>();
        if (data == null || data.isBlank()) {
            return parameters;
        }
        String[] pairs = data.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue[0].isBlank()) {
                continue;
            }
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = "";
            if (keyValue.length > 1) {
                value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
            }
            parameters.put(key, value);
        }
        return parameters;
    }

    public void merge(final QueryParameter other) {
        if (other == null) {
            return;
        }
        queryParameters.putAll(other.queryParameters);
    }

    public String getValue(String key) {
        return queryParameters.get(key);
    }
}
