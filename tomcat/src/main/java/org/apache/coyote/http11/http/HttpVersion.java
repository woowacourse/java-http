package org.apache.coyote.http11.http;

public enum HttpVersion {
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("HTTP/2"),
    ;

    private final String text;

    HttpVersion(String text) {
        this.text = text;
    }

    public static HttpVersion from(String value) {
        for (HttpVersion v : values()) {
            if (v.text.equalsIgnoreCase(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 HTTP 버전: " + value);
    }

    @Override
    public String toString() {
        return text;
    }
}
