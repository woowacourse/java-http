package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpStatusCode;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeader responseHeader;
    private final ResponseBody responseBody;

    private HttpResponse(
            final StatusLine statusLine,
            final ResponseHeader responseHeader,
            final ResponseBody responseBody
    ) {
        this.statusLine = statusLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final HttpStatusCode httpStatusCode, final ResponseBody responseBody) {
        final ResponseHeader responseHeader = ResponseHeader.from(responseBody);
        final StatusLine statusLine = new StatusLine(httpStatusCode);
        return new HttpResponse(statusLine, responseHeader, responseBody);
    }

    public static HttpResponse withCookie(
            final HttpStatusCode httpStatusCode,
            final ResponseBody responseBody,
            final HttpCookie httpCookie
    ) {
        final ResponseHeader responseHeader = ResponseHeader.from(responseBody);
        responseHeader.addCookie(httpCookie);
        final StatusLine statusLine = new StatusLine(httpStatusCode);
        return new HttpResponse(statusLine, responseHeader, responseBody);
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
        return statusLine + "\r\n" +
                responseHeader + "\r\n" +
                responseBody.getContent();
    }
}
