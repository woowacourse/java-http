package org.apache.coyote.http11;

public enum HttpMethod {
    GET,
    POST,
    ;

    public static HttpMethod from(String httpMethod) {
        try {
            return valueOf(httpMethod);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("지원되지 않는 HTTP 메서드 입니다.");
        }
    }
}
