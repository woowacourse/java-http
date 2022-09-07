package org.apache.coyote.http11.model.request;

import java.util.Arrays;

public enum Method {

    GET,
    POST,
    ;

    public static Method findByName(final String methodName) {
        return Arrays.stream(Method.values())
                .filter(method -> method.name().equals(methodName.toUpperCase()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
