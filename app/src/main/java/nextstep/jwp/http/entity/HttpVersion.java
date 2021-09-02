package nextstep.jwp.http.entity;

import java.util.Arrays;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1");

    private final String protocol;

    HttpVersion(String protocol) {
        this.protocol = protocol;
    }

    public static HttpVersion of(String name) {
        return Arrays.stream(HttpVersion.values())
                .filter(httpVersion -> httpVersion.hasProtocol(name))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String protocol() {
        return protocol;
    }

    private boolean hasProtocol(String protocol) {
        return this.protocol.equals(protocol);
    }
}
