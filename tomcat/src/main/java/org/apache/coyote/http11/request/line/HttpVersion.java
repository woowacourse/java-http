package org.apache.coyote.http11.request.line;

import java.util.Arrays;

/**
 * @see <a href="https://developer.mozilla.org/ko/docs/Web/HTTP/Guides/Evolution_of_HTTP"> HTTP의 진화 </a>
 * */

public enum HttpVersion {

    HTTP_0_9("HTTP/0.9"),
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2_0("HTTP/2.0"),
    HTTP_3_0("HTTP/3.0"),
    ;

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion from(String value) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.version.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 버전입니다: " + value));
    }

    public String getVersion() {
        return version;
    }

}
