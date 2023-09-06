package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2_0("HTTP/2.0");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion findVersion(final String version) {
        return Arrays.stream(values())
                .filter(it -> it.version.equals(version))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 Http 버전입니다."));
    }

    public String getVersion() {
        return version;
    }
}
