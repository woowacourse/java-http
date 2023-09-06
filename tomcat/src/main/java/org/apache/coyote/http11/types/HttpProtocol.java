package org.apache.coyote.http11.types;

import java.util.Arrays;

public enum HttpProtocol {
    HTTP_1_1("HTTP/1.1"),
    ;

    private final String protocol;

    HttpProtocol(String protocol) {
        this.protocol = protocol;
    }

    public static HttpProtocol from(String protocol) {
        return Arrays.stream(values()).filter(it -> it.protocol.equalsIgnoreCase(protocol))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 메소드입니다."));
    }

    public String getProtocol() {
        return protocol;
    }
}
