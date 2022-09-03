package org.apache.coyote.http11;

public enum StatusCode {

    OK(200, " 200 OK ");

    private final int status;
    private final String statusMessage;

    StatusCode(final int status, final String statusMessage) {
        this.status = status;
        this.statusMessage = statusMessage;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
