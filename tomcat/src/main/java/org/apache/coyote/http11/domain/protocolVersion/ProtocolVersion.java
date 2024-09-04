package org.apache.coyote.http11.domain.protocolVersion;

import java.util.List;

public class ProtocolVersion {

    private static final int PROTOCOL_INDEX = 0;
    private static final int VERSION_INDEX = 1;

    private final Protocol protocol;
    private final Version version;

    public ProtocolVersion(String inputProtocolVersion) {
        List<String> protocolVersion = List.of(inputProtocolVersion.split("/"));

        String protocol = protocolVersion.get(PROTOCOL_INDEX);
        String version = protocolVersion.get(VERSION_INDEX);

        this.protocol = Protocol.findProtocol(protocol);
        this.version = new Version(version);
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public String getVersionValue() {
        return version.getVersion();
    }
}
