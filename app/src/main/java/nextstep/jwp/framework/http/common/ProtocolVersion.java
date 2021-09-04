package nextstep.jwp.framework.http.common;

public class ProtocolVersion {

    private final String value;

    public ProtocolVersion(String protocolVersion) {
        this.value = protocolVersion;
    }

    public String getValue() {
        return value;
    }
}
