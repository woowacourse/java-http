package org.apache.coyote.http11;

public class HttpResponse {

    private final StatusLine statusLine;
    private final HttpResponseHeader httpResponseHeader;
    private final String responseBody;

    public HttpResponse(StatusLine statusLine, HttpResponseHeader httpResponseHeader, String responseBody) {
        this.statusLine = statusLine;
        this.httpResponseHeader = httpResponseHeader;
        this.responseBody = responseBody;
    }

    public boolean hasResponseBody() {
        return this.responseBody != null;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public HttpResponseHeader getHeader() {
        return this.httpResponseHeader;
    }
}
