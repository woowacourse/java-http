package org.apache.coyote.http11.httpRequest;

import java.util.Arrays;

public enum RequestMethod {

    GET,
    POST,
    ;

    public static RequestMethod parse(final String value) {
        return Arrays.stream(values())
                .filter(method -> value.equalsIgnoreCase(method.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 요청 메서드입니다: " + value));
    }
}
