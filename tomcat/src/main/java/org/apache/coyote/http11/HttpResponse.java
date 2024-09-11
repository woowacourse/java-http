package org.apache.coyote.http11;

public class HttpResponse {

    private final StatusLine statusLine;
    private final HttpHeader httpHeader;

    public HttpResponse(StatusLine statusLine, HttpHeader httpHeader) {
        this.statusLine = statusLine;
        this.httpHeader = httpHeader;
    }
}
