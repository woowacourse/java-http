package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum RequestMethod {

    GET,
    POST,
    ;

    public static RequestMethod from(final String value) {
        return Arrays.stream(values())
                .filter(requestMethod -> requestMethod.name().equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new RuntimeException("해당 메서드는 지원하지 않습니다."));
    }
}
