package org.apache.coyote.http11.response;

import org.apache.coyote.http11.cookie.Cookie;

public class HttpResponse {
    private StatusLine statusLine;
    private ResponseHeaders responseHeaders;
    private ResponseBody responseBody;

    private HttpResponse(final StatusLine statusLine,
                         final ResponseHeaders responseHeaders,
                         final ResponseBody responseBody) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse create() {
        return new HttpResponse(
                StatusLine.EMPTY,
                ResponseHeaders.EMPTY,
                ResponseBody.EMPTY
        );
    }

    public void redirect(final String redirectPath) {
        this.statusLine.setHttpStatusLine(HttpStatus.FOUND);
        this.responseHeaders = ResponseHeaders.redirect(redirectPath);
    }

    public void addSession(final String sessionId) {
        responseHeaders.addCookie(new Cookie("JSESSIONID", sessionId));
    }

    public String getCookieValue() {
        return responseHeaders.getCookieValues();
    }

    public void setHttpStatus(final HttpStatus httpStatus) {
        this.statusLine.setHttpStatusLine(httpStatus);
    }

    public void setResponseBody(final ResponseBody responseBody) {
        this.responseBody = responseBody;
    }

    public void setResponseHeaders(final ResponseBody responseBody) {
        this.responseHeaders = ResponseHeaders.from(responseBody);
    }

    @Override
    public String toString() {
        return statusLine.getStatusLine() + "\r\n" +
                responseHeaders.toString() +
                "" + "\r\n" +
                new String(responseBody.getContent());
    }
}
