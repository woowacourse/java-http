package org.apache.coyote.http.request;

import java.util.Arrays;

public enum ContentType {

    FORM_URLENCODED("application/x-www-form-urlencoded"),
    JSON("application/json"),
    PLAIN("text/plain");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ContentType from(String header) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.matches(header))
                .findFirst()
                .orElse(PLAIN);
    }

    public boolean matches(String header) {
        return header != null && header.startsWith(value);
    }
}
