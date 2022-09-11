package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum RequestMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    OPTIONS;


    public static RequestMethod findMethod(String string) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(string))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Request Method " + string + " is unsupported"));
    }
}
