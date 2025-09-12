package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpVersion {
    ONE_ONE("HTTP/1.1");

    private final String version;

    public static HttpVersion from(String version) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.version.equals(version))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 버전입니다."));
    }

    HttpVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
