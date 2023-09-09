package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.http11.HttpSpecException.HttpProtocolException;

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
                .orElseThrow(HttpProtocolException::new);
    }

    public String getValue() {
        return value;
    }
}
