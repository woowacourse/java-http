package org.apache.coyote.http11.httpmessage.response;

public class HttpStatusLine {

    private final String httpVersion;

    private int statusCode;
    private String statusText;

    public HttpStatusLine(String httpVersion, int statusCode, String statusText) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public String toHttpMessage() {
        return String.format("%s %s %s ", httpVersion, statusCode, statusText);
    }

    public void setStatus(HttpStatus status) {
        this.statusCode = status.getStatusCode();
        this.statusText = status.getStatusText();
    }
}
