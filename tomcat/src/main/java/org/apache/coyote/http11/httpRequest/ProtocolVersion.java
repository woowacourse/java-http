package org.apache.coyote.http11.httpRequest;

import java.util.Arrays;

public enum ProtocolVersion {

    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("HTTP/2"),
    ;

    private final String version;

    ProtocolVersion(final String version) {
        this.version = version;
    }

    public static ProtocolVersion parse(final String value) {
        return Arrays.stream(values())
                .filter(protocolVersion -> value.equals(protocolVersion.version))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 프로토콜 버전입니다: " + value));
    }

    public String getVersion() {
        return this.version;
    }
}
