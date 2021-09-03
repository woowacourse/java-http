package nextstep.jwp.model;

import java.io.IOException;
import java.util.Arrays;

public enum ProtocolType {

    HTTP_1_1("HTTP/1.1");

    private final String value;

    ProtocolType(String value) {
        this.value = value;
    }

    public static ProtocolType find(String protocol) throws IOException {
        return Arrays.stream(ProtocolType.values())
                .filter(x -> protocol.equals(x.value))
                .findFirst()
                .orElseThrow(IOException::new);
    }

    public String value() {
        return value;
    }
}
