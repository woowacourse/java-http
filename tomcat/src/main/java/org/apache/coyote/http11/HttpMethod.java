package org.apache.coyote.http11;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    ;

    public static HttpMethod parseHttpMethod(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 HTTP 메서드입니다.");
        }
    }
}
