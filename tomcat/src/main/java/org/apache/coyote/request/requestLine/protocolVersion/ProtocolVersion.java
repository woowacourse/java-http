package org.apache.coyote.request.requestLine.protocolVersion;

public class ProtocolVersion {

    public static final String PROTOCOL_VERSION_SEPARATOR = "/";
    public static final int PROTOCOL_INDEX = 0;
    public static final int VERSION_INDEX = 1;

    private Protocol protocol;
    private Version version;

    public static ProtocolVersion from(final String protocolVersion) {
        final String[] splitProtocolVersion = protocolVersion.split(PROTOCOL_VERSION_SEPARATOR);
        return new ProtocolVersion(Protocol.from(splitProtocolVersion[PROTOCOL_INDEX]), Version.from(splitProtocolVersion[VERSION_INDEX]));
    }

    public static ProtocolVersion of(final Protocol protocol, final Version version) {
        return new ProtocolVersion(protocol, version);
    }

    private ProtocolVersion(final Protocol protocol, final Version version) {
        this.protocol = protocol;
        this.version = version;
    }

    public String combine() {
        return protocol.name() + PROTOCOL_VERSION_SEPARATOR + version.getVersion();
    }
}
