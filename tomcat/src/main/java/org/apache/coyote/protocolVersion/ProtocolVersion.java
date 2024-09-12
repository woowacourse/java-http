package org.apache.coyote.protocolVersion;

import java.util.Objects;
import org.apache.coyote.util.Protocol;

public class ProtocolVersion {

    private static final String PROTOCOL_VERSION_COMBINATOR = "/";

    private final Protocol protocol;
    private final Version version;

    private ProtocolVersion(Protocol protocol, Version version) {
        this.protocol = protocol;
        this.version = version;
    }

    public static ProtocolVersion ofHTTP1() {
        return new ProtocolVersion(Protocol.HTTP, new Version("1.1"));
    }

    public String getCombinedProtocolVersion() {
        return protocol.name() + PROTOCOL_VERSION_COMBINATOR + getVersionValue();
    }

    private String getVersionValue() {
        return version.getVersion();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProtocolVersion that = (ProtocolVersion) o;
        return protocol == that.protocol && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocol, version);
    }
}
