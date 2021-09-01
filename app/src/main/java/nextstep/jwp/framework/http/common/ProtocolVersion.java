package nextstep.jwp.framework.http.common;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProtocolVersion that = (ProtocolVersion) o;
        return Objects.equals(protocol, that.protocol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocol);
    }
}
