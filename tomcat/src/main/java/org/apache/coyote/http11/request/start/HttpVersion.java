package org.apache.coyote.http11.request.start;

import java.util.Arrays;

public enum HttpVersion {
    V1("HTTP/0.9"),
    V2("HTTP/1.0"),
    V3("HTTP/1.1"),
    ;

    private final String version;

    HttpVersion(final String version) {
        this.version = version;
    }

    public static HttpVersion from(final String httpVersion) {
        return Arrays.stream(HttpVersion.values())
                .filter(version -> version.getVersion().equals(httpVersion))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 http요청입니다."));
    }

    public String getVersion() {
        return version;
    }
}
