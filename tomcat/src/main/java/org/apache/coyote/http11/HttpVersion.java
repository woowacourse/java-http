package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("HTTP/2");

    private final String name;

    HttpVersion(final String name) {
        this.name = name;
    }

    public static HttpVersion from(final String version) {
        return Arrays.stream(HttpVersion.values())
                .filter(v -> v.name.equals(version))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("HTTP Version not supported: " + version));
    }

    public String getName() {
        return name;
    }
}
