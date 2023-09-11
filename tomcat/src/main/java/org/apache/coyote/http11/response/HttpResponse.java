package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpCookie;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private StatusLine statusLine;
    private ResponseHeader responseHeader;
    private ResponseBody responseBody;

    public void setStatusLine(final StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void setResponseHeader(final ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    public void setResponseBody(final ResponseBody responseBody) {
        this.responseBody = responseBody;
    }

    public void setCookie(final HttpCookie httpCookie) {
        this.responseHeader.addCookie(httpCookie);
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public ResponseHeader getResponseHeaders() {
        return responseHeader;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }

    public String toString() {
        return statusLine + CRLF +
                responseHeader + CRLF +
                responseBody.getContent();
    }
}
