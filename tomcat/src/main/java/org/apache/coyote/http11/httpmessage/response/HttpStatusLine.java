package org.apache.coyote.http11.httpmessage.response;

public class HttpStatusLine {

    private String httpVersion;
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

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setMethodFound() {
        this.statusCode = 301;
        this.statusText = "FOUND";
    }

    public void setMethodOK() {
        this.statusCode = 200;
        this.statusText = "OK";
    }

    public void setMethodBadRequest() {
        this.statusCode = 400;
        this.statusText = "BAD REQUEST";
    }
}
