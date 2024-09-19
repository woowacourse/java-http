package org.apache.coyote.http11.response;

public class StatusLine {

    private final String protocol;
    private final Status status;

    public StatusLine(String protocol, Status status) {
        this.protocol = protocol;
        this.status = status;
    }

    public String getStatusLine() {
        return protocol + " " + status.getCode() + " " + status.getMessage();
    }
}
