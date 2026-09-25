package org.apache.coyote.http11;

public enum HttpMethod {
    GET,
    POST
    ;

    public static HttpMethod from(String value) {
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("존재하지 않는 메서드입니다.");
        }
    }
}
