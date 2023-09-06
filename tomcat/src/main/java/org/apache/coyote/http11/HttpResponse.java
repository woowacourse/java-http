package org.apache.coyote.http11;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;

    private HttpResponse(
            final StatusLine statusLine,
            final ResponseHeaders responseHeaders,
            final ResponseBody responseBody
    ) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final HttpStatusCode httpStatusCode, final ResponseBody responseBody) {
        final ResponseHeaders responseHeaders = ResponseHeaders.from(responseBody);
        final StatusLine statusLine = new StatusLine(httpStatusCode);
        return new HttpResponse(statusLine, responseHeaders, responseBody);
    }

    public static HttpResponse withCookie(
            final HttpStatusCode httpStatusCode,
            final ResponseBody responseBody,
            final HttpCookie httpCookie
    ) {
        final ResponseHeaders responseHeaders = ResponseHeaders.from(responseBody);
        responseHeaders.addCookie(httpCookie);
        final StatusLine statusLine = new StatusLine(httpStatusCode);
        return new HttpResponse(statusLine, responseHeaders, responseBody);
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public ResponseHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }

    public String toString() {
        return statusLine + "\r\n" +
                responseHeaders + "\r\n" +
                responseBody.getContent();
    }
}
