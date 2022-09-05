package org.apache.coyote;

import java.util.Arrays;
import org.apache.coyote.exception.InvalidHttpMethodException;

public enum HttpMethod {

    GET("GET"),
    POST("POST");

    private final String value;

    HttpMethod(final String value) {
        this.value = value;
    }

    public static HttpMethod from(final String value) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.isSameValue(value))
                .findAny()
                .orElseThrow(InvalidHttpMethodException::new);
    }

    private boolean isSameValue(final String value) {
        return this.value.equalsIgnoreCase(value);
    }

    public String getValue() {
        return value;
    }
}
