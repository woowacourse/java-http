package org.apache.coyote.http11.message;

import java.util.Arrays;

public enum HttpProtocol {
    HTTP_ONE("HTTP/1.1");

    private final String httpVersion;

    HttpProtocol(final String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public static HttpProtocol from(final String requestProtocol) {
        return Arrays.stream(values())
            .filter(protocol -> protocol.httpVersion.equals(requestProtocol))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 프로토콜입니다."));
    }

    public String getVersion() {
        return httpVersion;
    }
}
