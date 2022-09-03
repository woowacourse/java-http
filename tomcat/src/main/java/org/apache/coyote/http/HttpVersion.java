package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpVersion {

    HTTP11("HTTP/1.1"),
    ;

    private final String version;

    HttpVersion(final String version) {
        this.version = version;
    }

    public static HttpVersion findVersion(String version) {
        return Arrays.stream(values())
                .filter(value -> value.version.equalsIgnoreCase(version))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("버전 정보가 올바르지 않습니다."));
    }

    public String getVersion() {
        return version;
    }
}
