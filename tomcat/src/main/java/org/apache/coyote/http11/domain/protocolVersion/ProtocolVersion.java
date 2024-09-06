package org.apache.coyote.http11.domain.protocolVersion;

import java.util.List;

public class ProtocolVersion {

    private static final int PROTOCOL_INDEX = 0;
    private static final int VERSION_INDEX = 1;
    private static final String PROTOCOL_VERSION_COMBINATOR = "/";

    private final Protocol protocol;
    private final Version version;

    public ProtocolVersion(String inputProtocolVersion) {
        List<String> protocolVersion = List.of(inputProtocolVersion.split(PROTOCOL_VERSION_COMBINATOR));

        String protocol = protocolVersion.get(PROTOCOL_INDEX);
        String version = protocolVersion.get(VERSION_INDEX);

        this.protocol = Protocol.findProtocol(protocol);
        this.version = new Version(version);
    }
    
    public String getCombinedProtocolVersion() {
        return protocol.name() + PROTOCOL_VERSION_COMBINATOR + getVersionValue();
    }

    private String getVersionValue() {
        return version.getVersion();
    }
}
