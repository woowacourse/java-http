package org.apache.coyote.http11.message.response;

public class StatusLine {

    private static final String DELIMITER = " ";

    private final String protocolVersion;
    private final StatusCode statusCode;

    public StatusLine(String protocolVersion, StatusCode statusCode) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
    }

    public String convertMessage() {
        return String.join(DELIMITER, protocolVersion, statusCode.getCode(), statusCode.getDisplayName());
    }

    @Override
    public String toString() {
        return "HttpStatusLine{" +
                "protocolVersion='" + protocolVersion + '\'' +
                ", httpStatusCode=" + statusCode +
                '}';
    }
}
