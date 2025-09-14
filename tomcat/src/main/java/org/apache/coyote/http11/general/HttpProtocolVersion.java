package org.apache.coyote.http11.general;

import java.util.Arrays;

public enum HttpProtocolVersion {

    HTTP11("HTTP/1.1");

    private final String version;

    HttpProtocolVersion(String version) {
        this.version = version;
    }

    public static HttpProtocolVersion from(String version) {
        return Arrays.stream(values())
            .filter(value -> value.version.equals(version))
            .findFirst()
            .orElse(null);
    }

    public String getVersion() {
        return version;
    }
}
