package org.apache.coyote.request;

public enum HttpMethod {
    POST,
    GET,
    DELETE,
    PUT,
    PATCH;

    public static HttpMethod from(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 HTTP 메서드입니다: " + method, e);
        }
    }
}
