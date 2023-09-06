package org.apache.coyote.http11;

import java.util.Arrays;

public enum Protocol {

    HTTP1_1("HTTP/1.1");

    public final String value;

    Protocol(String value) {
        this.value = value;
    }

    public static Protocol from(String protocolValue) {
        return Arrays.stream(values())
                .filter(protocol -> protocol.getValue().equals(protocolValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 네트워크 프로토콜 요청입니다.")); // 변경 요망
    }

    public String getValue() {
        return value;
    }
}
