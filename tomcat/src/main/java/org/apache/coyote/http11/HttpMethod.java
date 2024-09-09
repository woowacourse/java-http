package org.apache.coyote.http11;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH,
    ;

    public static HttpMethod findByMethodName(String requestMethod) {
        return Stream.of(values())
                .filter(method -> method.name().equals(requestMethod))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(requestMethod + "에 해당하는 메소드가 없습니다."));
    }
}
