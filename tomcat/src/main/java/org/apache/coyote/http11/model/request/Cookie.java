package org.apache.coyote.http11.model.request;

import java.util.HashMap;
import java.util.Map;

public record Cookie(
        Map<String, String> values
) {
    public static Cookie from(String cookieForm) {
        if (cookieForm.isBlank()) {
            return new Cookie(Map.of());
        }
        Map<String, String> values = new HashMap<>();
        String[] parameters = cookieForm.split(";");
        for (String parameter : parameters) {
            String[] nameAndValue = parameter.trim().split("=", 2);
            if (nameAndValue.length != 2) {
                throw new IllegalArgumentException(
                        "올바르지 않은 쿼리 파라미터입니다: " + parameter
                );
            }
            values.put(nameAndValue[0], nameAndValue[1]);
        }
        return new Cookie(values);
    }

    public boolean hasCookie(String value) {
        return values.containsKey(value);
    }

    public String getCookie(String value) {
        return values.get(value);
    }
}
