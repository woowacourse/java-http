package org.apache.coyote.http11;

public enum ProtocolVersion {
    HTTP11("HTTP/1.1"),
    HTTP2("HTTP/2"),
    HTTP3("HTTP/3"),
    ;

    private final String protocolVersionLabel;

    ProtocolVersion(String protocolVersionLabel) {
        this.protocolVersionLabel = protocolVersionLabel;
    }

    public static ProtocolVersion fromHeaderValue(String headerValue) {
        for (ProtocolVersion protocolVersion : ProtocolVersion.values()) {
            if (protocolVersion.protocolVersionLabel.equals(headerValue)) {
                return protocolVersion;
            }
        }
        throw new IllegalArgumentException("잘못된 Protocol 입니다. : " + headerValue);
    }

    public String getResponseHeader() {
        return this.protocolVersionLabel;
    }
}
