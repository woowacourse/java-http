package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpProtocol {
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("HTTP/2.0"),
    HTTP_3("HTTP/3.0");

    private final String version;

    HttpProtocol(String version) {
        this.version = version;
    }

    public static HttpProtocol getHttpProtocol(String version) {
        return Arrays.stream(HttpProtocol.values())
                .filter(protocol -> protocol.version.equalsIgnoreCase(version))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String getVersion() {
        return version;
    }
}
