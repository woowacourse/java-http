package org.apache.coyote.http11.response;

public class HttpResponse {

    private StatusLine statusLine;
    private ResponseHeaders responseHeaders;
    private String responseBody;

    public HttpResponse() {
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
}
