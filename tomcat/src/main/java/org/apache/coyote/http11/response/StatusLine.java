package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.response.HttpStatusCode.OK;

public class StatusLine {
    private String version;
    private HttpStatusCode statusCode;
    private String statusMessage;

    private StatusLine(String version, HttpStatusCode statusCode, String statusMessage) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public static StatusLine defaultStatusLine() {
        return new StatusLine("HTTP/1.1", OK, OK.getName());
    }

    public String getVersion() {
        return version;
    }

    public int getStatusCode() {
        return statusCode.getCode();
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
        this.statusMessage = statusCode.getName();
    }
}
