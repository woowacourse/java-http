package org.apache.coyote.request.requestLine.protocolVersion;

import java.util.Arrays;

public enum Protocol {

    HTTP,
    HTTPS;

    public static Protocol from(final String inputProtocol) {
        return Arrays.stream(Protocol.values())
                .filter(protocol -> protocol.name().equals(inputProtocol))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 지원하지 않는 프로토콜입니다."));
    }
}
