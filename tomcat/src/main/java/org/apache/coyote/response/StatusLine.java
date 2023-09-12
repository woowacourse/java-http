package org.apache.coyote.response;

import org.apache.coyote.Protocol;

public class StatusLine {

    private final Protocol protocol;
    private Status status;

    public StatusLine(final Protocol protocol, final Status status) {
        this.protocol = protocol;
        this.status = status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public String stringify() {
        return String.join(" ",
                protocol.getValue(),
                Integer.toString(status.getCode()),
                status.getMessage()
        ) + " ";
    }
}
