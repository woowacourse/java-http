package nextstep.jwp.framework.http.common;

public class ProtocolVersion {

    private static final String DEFAULT_VERSION = "HTTP/1.1";

    private final String protocol;

    public ProtocolVersion(final String protocol) {
        this.protocol = protocol;
    }

    public static ProtocolVersion of(final String protocol) {
        return new ProtocolVersion(protocol);
    }

    public static ProtocolVersion defaultVersion() {
        return new ProtocolVersion(DEFAULT_VERSION);
    }

    public String getProtocol() {
        return protocol;
    }
}
