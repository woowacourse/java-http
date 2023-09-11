package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum HttpProtocol {
    HTTP_1_1("HTTP/1.1");

    private final String protocol;

    HttpProtocol(String protocol) {
        this.protocol = protocol;
    }

    public static HttpProtocol from(String protocol) {
        return Arrays.stream(values())
                .filter(value -> value.protocol.equals(protocol))
                .findFirst()
                .orElseThrow();
    }

    public String message() {
        return protocol + " ";
    }
}
