package org.apache.coyote;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2_0("HTTP/2.0");

    private final String versionString;

    HttpVersion(String versionString) {
        this.versionString = versionString;
    }

    public static HttpVersion from(String versionString) {
        return Arrays.stream(HttpVersion.values())
                .filter(version -> version.getVersionString().equals(versionString))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 HTTP 버전입니다. HTTP 버전 = " + versionString));
    }

    public String getVersionString() {
        return versionString;
    }
}
