package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpProtocolVersion {
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2_0("HTTP/2.0"),
    ;

    private final String version;

    HttpProtocolVersion(final String version) {
        this.version = version;
    }

    public static HttpProtocolVersion valueOfVersion(final String target) {
        return Arrays.stream(values())
                .filter(value -> value.version.equals(target))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 버전입니다. " + target));
    }

    public String getVersion() {
        return version;
    }
}
