package org.apache.tomcat.util.http;

import java.util.Arrays;

public enum HttpVersion {
    HTTP11("HTTP/1.1");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public static HttpVersion of(String value) {
        return Arrays.stream(values())
                .filter(version -> version.version.equals(value))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("요청하신 HTTP Version은 지원하지 않습니다."));
    }
}
