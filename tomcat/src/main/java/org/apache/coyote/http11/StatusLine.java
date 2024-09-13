package org.apache.coyote.http11;

public class StatusLine {

    private static final String DELIMITER = " ";
    private final String version;
    private final int statusCode;
    private final String statusMessage;

    public StatusLine(String version, Status status) {
        this.version = version;
        this.statusCode = status.getStatusCode();
        this.statusMessage = status.getStatusMessage();
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getStatusLineToString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(version).append(DELIMITER)
                .append(statusCode).append(DELIMITER)
                .append(statusMessage).append(DELIMITER);
        return stringBuilder.toString();
    }
}
