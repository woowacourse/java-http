package org.apache.coyote.http;

import java.util.Arrays;

public enum HttpVersion {
    HTTP11("HTTP/1.1"),
    ;

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public static HttpVersion findVersionByProtocolVersion(String version) {
        return Arrays.stream(HttpVersion.values()).filter(httpVersion -> httpVersion.getVersion().equals(version)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown http version: " + version));
    }
}
