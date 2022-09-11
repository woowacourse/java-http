package org.apache.coyote.http11.header;

import java.util.Arrays;

public enum HttpVersion {
    HTTP11("HTTP/1.1");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion findVersion(String version) {
        return Arrays.stream(values())
                .filter(value -> value.version.equalsIgnoreCase(version))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("HTTP version " + version + " not supported"));
    }

    public String getVersion() {
        return version;
    }
}
