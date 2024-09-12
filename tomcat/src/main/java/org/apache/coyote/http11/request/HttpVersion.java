package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1"),
    ;

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion from(String version) {
        String message = "지원하지 않는 버전입니다. version: %s".formatted(version);

        return Arrays.stream(values())
                .filter(value -> value.version.equals(version))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(message));
    }

    public String getVersion() {
        return version;
    }
}
