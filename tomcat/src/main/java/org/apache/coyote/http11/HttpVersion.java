package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpVersion {

    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion parse(String versionValue) {
        return Arrays.stream(values())
                .filter(version -> version.value.equals(versionValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP Version입니다: " + versionValue));
    }

    public String getValue() {
        return value;
    }
}
