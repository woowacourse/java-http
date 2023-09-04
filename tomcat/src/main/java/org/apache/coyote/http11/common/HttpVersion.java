package org.apache.coyote.http11.common;

import org.apache.coyote.http11.exception.InvalidHttpVersionException;

import java.util.Arrays;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1"),
    ;
    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion of(String version) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.version.equalsIgnoreCase(version))
                .findFirst()
                .orElseThrow(InvalidHttpVersionException::new);
    }

    public String getVersion() {
        return version;
    }
}
