package org.apache.coyote.http11;

public enum Method {
    GET,
    POST;

    public static Method from(final String value) {
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 HTTP 메서드입니다: " + value, e);
        }
    }

    public boolean isGet() {
        return this == GET;
    }

    public boolean isPost() {
        return this == POST;
    }
}
