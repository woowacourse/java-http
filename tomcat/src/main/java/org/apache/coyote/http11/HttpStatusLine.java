package org.apache.coyote.http11;

public class HttpStatusLine {

    private static final String DELIMITER = " ";

    private final String protocolVersion;
    private final HttpStatusCode httpStatusCode;

    public HttpStatusLine(String protocolVersion, HttpStatusCode httpStatusCode) {
        this.protocolVersion = protocolVersion;
        this.httpStatusCode = httpStatusCode;
    }

    public String convertMessage() {
        return String.join(DELIMITER, protocolVersion, httpStatusCode.getCode(), httpStatusCode.getDisplayName());
    }

    @Override
    public String toString() {
        return "HttpStatusLine{" +
                "protocolVersion='" + protocolVersion + '\'' +
                ", httpStatusCode=" + httpStatusCode +
                '}';
    }
}
