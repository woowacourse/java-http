package org.apache.coyote.http11.model.request;

import java.util.HashMap;
import java.util.Map;

public record QueryParameters(
        Map<String, String> values
) {

    public QueryParameters {
        values = Map.copyOf(values);
    }

    public static QueryParameters from(String queryString) {
        if (queryString.isBlank()) {
            return empty();
        }

        Map<String, String> values = new HashMap<>();
        String[] parameters = queryString.split("&");
        for (String parameter : parameters) {
            String[] nameAndValue = parameter.split("=", 2);
            if (nameAndValue.length != 2) {
                throw new IllegalArgumentException(
                        "올바르지 않은 쿼리 파라미터입니다: " + parameter
                );
            }
            values.put(nameAndValue[0], nameAndValue[1]);
        }

        return new QueryParameters(values);
    }

    public static QueryParameters empty() {
        return new QueryParameters(Map.of());
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}
