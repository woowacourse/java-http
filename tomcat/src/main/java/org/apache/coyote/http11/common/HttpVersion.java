package org.apache.coyote.http11.common;

import org.apache.coyote.http11.exception.IllegalHttpVersionException;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1");

    private final String name;

    HttpVersion(final String name) {
        this.name = name;
    }

    public static HttpVersion from(final String httpVersionName) {
        return Arrays.stream(HttpVersion.values())
                     .filter(httpVersion -> httpVersion.name.equals(httpVersionName))
                     .findAny()
                     .orElseThrow(() -> new IllegalHttpVersionException("HTTP 버전 정보가 올바르지 않습니다."));
    }

    public String getName() {
        return name;
    }
}
