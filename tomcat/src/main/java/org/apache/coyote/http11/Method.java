package org.apache.coyote.http11;

public enum Method {
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE,
    PATCH,
    ;

    public static Method fromHeaderValue(String headerValue) {
        for (Method value : Method.values()) {
            if (value.name().equals(headerValue)) {
                return value;
            }
        }
        throw new IllegalArgumentException("잘못된 Method 입니다. :" + headerValue);
    }
}
