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

    public boolean isPost() {
        return this.equals(POST);
    }

    public boolean isGet() {
        return this.equals(GET);
    }

    public boolean isValidMethod() {
        return !this.equals(NONE);
    }
}
