package nextstep.jwp.model.response;

import nextstep.jwp.model.ProtocolType;

public class StatusLine {

    private final ProtocolType protocol;
    private final StatusType status;

    public StatusLine(ProtocolType protocol, StatusType statusCode) {
        this.protocol = protocol;
        this.status = statusCode;
    }

    @Override
    public String toString() {
        return String.format("%s %d %s ", protocol.value(), status.code(), status.text());
    }
}
