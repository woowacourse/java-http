package org.apache.coyote.http;

import java.util.Arrays;

public enum RequestMethod {

    GET,
    POST;

    RequestMethod() {
    }

    public static RequestMethod fromString(String method) {
        return Arrays.stream(RequestMethod.values())
                .filter(requestMethod -> requestMethod.name().equalsIgnoreCase(method))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public boolean isGetMethod() {
        return this.equals(GET);
    }

    public boolean isPostMethod() {
        return this.equals(POST);
    }
}
