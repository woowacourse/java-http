package org.apache.coyote.http11.response;

public class HttpResponse {
    private final StatusLine statusLine;
    private final HttpResponseHeader responseHeader;
    private final HttpResponseBody responseBody;

    public HttpResponse(StatusLine statusLine, HttpResponseHeader responseHeader, HttpResponseBody responseBody) {
        this.statusLine = statusLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }
}
