package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("HTTP/2"),
    HTTP_3("HTTP/3");

    private final String version;

    public static HttpVersion from(String version) {
        return Arrays.stream(HttpVersion.values())
                .filter(httpVersion -> httpVersion.version.equals(version))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown HTTP version: " + version));
    }

    HttpVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
