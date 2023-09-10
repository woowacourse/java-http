package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum HttpVersion {
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1");

    private final String version;

    HttpVersion(String name) {
        this.version = name;
    }

    public static HttpVersion from(final String field) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.version.equals(field))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 Http Version은 지원하지 않습니다."));
    }

    public String getVersion() {
        return version;
    }
}
