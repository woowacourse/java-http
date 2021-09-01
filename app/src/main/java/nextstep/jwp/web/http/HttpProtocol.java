package nextstep.jwp.web.http;

import java.util.Arrays;

public enum HttpProtocol {
    HTTP1_1("HTTP/1.1");

    private final String protocolName;

    HttpProtocol(String protocolName) {
        this.protocolName = protocolName;
    }

    public static HttpProtocol findByName(String protocol) {
        return Arrays.stream(values())
            .filter(it -> it.protocolName.equalsIgnoreCase(protocol))
            .findAny()
            .orElseThrow(
                () -> new RuntimeException(String.format("해당 이름의 프로토콜은 없습니다. -> %s", protocol)));
    }

    public String getProtocolName() {
        return protocolName;
    }
}
