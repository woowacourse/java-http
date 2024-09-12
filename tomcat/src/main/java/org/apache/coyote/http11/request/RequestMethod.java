package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum RequestMethod {

    GET,
    POST,
    ;

    public static RequestMethod find(String rawMethod) {
        return Arrays.stream(values())
                .filter(value -> value.name().equals(rawMethod))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 HTTP 메서드입니다."));
    }

    public boolean isGet() {
        return this == GET;
    }

    public boolean isPost() {
        return this == POST;
    }
}
