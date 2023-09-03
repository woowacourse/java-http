package org.apache.coyote.http11.common;

import java.util.Arrays;

import org.apache.coyote.http11.exception.InvalidHttpVersionException;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1");

    private final String version;

    HttpVersion(final String version) {
        this.version = version;
    }

    public static HttpVersion findByVersion(String version) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.version.equals(version))
                .findAny()
                .orElseThrow(() -> new InvalidHttpVersionException("유효하지 않은 HTTP version 입니다."));
    }

    public String getVersion() {
        return version;
    }
}
