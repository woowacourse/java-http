package org.apache.coyote.http11.response;

public class HttpResponse {

    private StatusLine statusLine;
    private ResponseHeaders responseHeaders;
    private String responseBody;

    public HttpResponse() {
    }

    public HttpResponse (String protocolVersion, String statusCode, String message) {
        this.statusLine = new StatusLine(protocolVersion, statusCode, message);
    }

    public HttpResponse(StatusLine statusLine, ResponseHeaders responseHeaders, String responseBody) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public ResponseHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void setResponseHeaders(ResponseHeaders responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
