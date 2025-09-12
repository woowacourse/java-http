package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1"),
    ;

    private final String httpVersion;

    HttpVersion(final String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public static HttpVersion parseHttpVersionFrom(final String extractedHttpVersion) {
        return Arrays.stream(HttpVersion.values())
                .filter(version -> version.httpVersion.equals(extractedHttpVersion))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 Http Version 입니다."));
    }
}
