package org.apache.coyote.common.request;

import java.util.Arrays;

public enum RequestMethod {

    GET("GET"),
    POST("POST")
    ;

    private final String value;

    RequestMethod(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RequestMethod of(final String method) {
        return Arrays.stream(values())
                .filter(m -> m.value.equals(method))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
