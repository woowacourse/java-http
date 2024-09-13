package org.apache.coyote.http11;

public class StatusLine {

    private static final String VERSION = "HTTP/1.1";
    private final String statusLine;

    private StatusLine(Status status) {
        this.statusLine = VERSION + " " + status.getCode() + " " + status.getName() + " ";
    }

    public static StatusLine from(Status status) {
        return new StatusLine(status);
    }

    public String getStatusLine() {
        return statusLine;
    }
}
