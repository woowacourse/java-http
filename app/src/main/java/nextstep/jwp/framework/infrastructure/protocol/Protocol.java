package nextstep.jwp.framework.infrastructure.protocol;

import java.util.Arrays;

public enum Protocol {
    HTTP1("HTTP/1.1");

    private final String signature;

    Protocol(String signature) {
        this.signature = signature;
    }

    public static Protocol findProtocol(String signature) {
        return Arrays.stream(Protocol.values())
            .filter(value -> value.signature.equals(signature))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("Invalid Protocol Request"));
    }
}
