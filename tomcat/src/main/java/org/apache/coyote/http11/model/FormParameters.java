package org.apache.coyote.http11.model;

import java.util.HashMap;
import java.util.Map;

public record FormParameters(
        Map<String, String> values
) {
    public static FormParameters from(String requestBody) {
        if (requestBody.isBlank()) {
            return new FormParameters(Map.of());
        }
        Map<String, String> values = new HashMap<>();
        String[] parameters = requestBody.split("&");
        for (String parameter : parameters) {
            String[] nameAndValue = parameter.split("=", 2);
            if (nameAndValue.length != 2) {
                throw new IllegalArgumentException(
                        "올바르지 않은 쿼리 파라미터입니다: " + parameter
                );
            }
            values.put(nameAndValue[0], nameAndValue[1]);
        }
        return new FormParameters(values);
    }

    public boolean hasQueryParameters() {
        return !values.isEmpty();
    }
}
