package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMethod {
    POST,
    GET,
    NONE,
    ;

    public static HttpMethod from(String method) {
        if (method == null) {
            return NONE;
        }

        return Arrays.stream(HttpMethod.values())
                .filter(value -> method.equalsIgnoreCase(value.name()))
                .findFirst()
                .orElse(NONE);
    }

    public boolean isMethod(HttpMethod httpMethod) {
        return httpMethod.equals(this);
    }

    public boolean isValidMethod() {
        return !this.equals(NONE);
    }
}
