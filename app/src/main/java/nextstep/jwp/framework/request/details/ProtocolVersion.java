package nextstep.jwp.framework.request.details;

public class ProtocolVersion {

    private static final String DEFAULT_VERSION = "HTTP/1.1";

    private String protocol;

    public ProtocolVersion(String protocol) {
        this.protocol = protocol;
    }

    public static ProtocolVersion of(String protocol) {
        return new ProtocolVersion(protocol);
    }

    public static ProtocolVersion defaultVersion() {
        return new ProtocolVersion(DEFAULT_VERSION);
    }

    public String getProtocol() {
        return protocol;
    }
}
