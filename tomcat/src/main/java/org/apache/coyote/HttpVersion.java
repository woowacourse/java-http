package org.apache.coyote;

import java.util.Arrays;

public enum HttpVersion {

    HTTP1_1("HTTP/1.1");

    private final String name;

    HttpVersion(String name) {
        this.name = name;
    }

    public static HttpVersion fromString(String version) {
        if (version == null) {
            throw new IllegalArgumentException("Version cannot be null");
        }
        return Arrays.stream(values())
                .filter(value -> version.equals(value.name))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("Cannot resolve Http Version from request: " + version));
    }

    public String getName() {
        return name;
    }
}
