package org.apache.coyote.response;

public class StatusLine {

    private static final String SEPERATOR = " ";
    private static final String BASIC_HTTP_VERSION = "HTTP/1.1";

    private final String version;
    private final int statusCode;
    private final String statusMessage;

    private StatusLine(String version, int statusCode, String statusMessage) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public static StatusLine from(int statusCode, String statusMessage) {
        return new StatusLine(BASIC_HTTP_VERSION, statusCode, statusMessage);
    }

    public String getStatusLine() {
        return BASIC_HTTP_VERSION + SEPERATOR + statusCode + SEPERATOR + statusMessage + SEPERATOR;
    }
}
