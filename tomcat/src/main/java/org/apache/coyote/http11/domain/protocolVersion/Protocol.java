package org.apache.coyote.http11.domain.protocolVersion;

import java.util.Arrays;

public enum Protocol {

    HTTP,
    HTTPS;

    public static Protocol findProtocol(String inputProtocol) {
        return Arrays.stream(Protocol.values())
                .filter(protocol -> protocol.name().equals(inputProtocol))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 프로토콜입니다."));
    }
}
