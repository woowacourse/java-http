package org.apache.coyote.http11.request;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod from(String value) {
        for (HttpMethod method : values()) {
            if (method.name().equals(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("[ERROR] 지원하지 않는 HTTP 메서드입니다. " + value);
    }
}
