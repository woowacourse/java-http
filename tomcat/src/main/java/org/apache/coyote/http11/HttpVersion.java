package org.apache.coyote.http11;

public enum HttpVersion {
    // 반드시 완전히 똑같이 써야한다. 대소문자 구분함.
    HTTP_1_1("HTTP/1.1");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion from(String target) {
        for (HttpVersion httpVersion : HttpVersion.values()) {
            if (httpVersion.value.equals(target)) {
                return httpVersion;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 HTTP 버전입니다.");
    }

    public String getValue() {
        return value;
    }
}
