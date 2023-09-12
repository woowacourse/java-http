package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.Protocol;
import org.apache.coyote.http11.common.Status;

public class StatusLine {

    private final Protocol protocol;
    private final Status status;

    public StatusLine(final Protocol protocol, final Status status) {
        this.protocol = protocol;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.join(" ", protocol.getValue(), String.valueOf(status.getCode()), status.toString());
    }
}
