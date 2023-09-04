package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpMethod {

    POST,
    GET,
    ;

    public static HttpMethod from(String methodName) {
        return Arrays.stream(values())
                .filter(it -> it.name().equalsIgnoreCase(methodName))
                .findFirst()
                .orElse(GET);
    }

    public String getValue() {
        return name().toUpperCase();
    }

}
