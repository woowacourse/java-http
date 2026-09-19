package org.apache.coyote;

import org.apache.coyote.exception.HttpParseException;

import java.util.Arrays;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    public static HttpMethod from(String value) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new HttpParseException("지원하지 않는 HTTP 메서드입니다: " + value));
    }
}
