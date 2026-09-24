package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QueryParameters {

    private final Map<String, String> values;

    private QueryParameters(Map<String, String> values) {
        this.values = values;
    }

    public static QueryParameters from(String queryString) {
        Map<String, String> values = new HashMap<>();

        if (queryString == null || queryString.isEmpty()) {
            return new QueryParameters(values);
        }

        for (String parameter : queryString.split("&")) {
            String[] keyValue = parameter.split("=", 2);
            if (keyValue.length == 2) {
                values.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
        }

        return new QueryParameters(values);
    }

    public Optional<String> find(String name) {
        return Optional.ofNullable(values.get(name));
    }
}
