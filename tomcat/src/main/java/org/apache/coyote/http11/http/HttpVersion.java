package org.apache.coyote.http11.http;

import java.util.Arrays;
import org.apache.catalina.exception.HttpVersionNotSupported;

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
        return Arrays.stream(HttpVersion.values())
                .filter(v -> v.text.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new HttpVersionNotSupported("지원하지 않는 HTTP 버전: " + value));
    }

    @Override
    public String toString() {
        return text;
    }
}
