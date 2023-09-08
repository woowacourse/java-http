package org.apache.coyote.http11.response;

import org.apache.coyote.http11.cookie.Cookie;

public class HttpResponse {
    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;

    private HttpResponse(final StatusLine statusLine,
                         final ResponseHeaders responseHeaders,
                         final ResponseBody responseBody) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final HttpStatus httpStatus,
                                  final ResponseBody responseBody) {
        return new HttpResponse(
                StatusLine.from(httpStatus),
                ResponseHeaders.from(responseBody),
                responseBody
        );
    }

    public static HttpResponse redirect(final HttpStatus httpStatus,
                                        final String redirectPath,
                                        final ResponseBody responseBody) {
        return new HttpResponse(
                StatusLine.from(httpStatus),
                ResponseHeaders.redirect(redirectPath),
                responseBody
        );
    }

    public static HttpResponse redirect(final HttpStatus httpStatus,
                                        final String redirectPath) {
        return new HttpResponse(
                StatusLine.from(httpStatus),
                ResponseHeaders.redirect(redirectPath),
                ResponseBody.noContent(ContentType.HTML)
        );
    }

    public void addSession(final String sessionId) {
        responseHeaders.addCookie(new Cookie("JSESSIONID", sessionId));
    }

    public String getCookieValue() {
        return responseHeaders.getCookieValues();
    }

    @Override
    public String toString() {
        return statusLine.getStatusLine() + "\r\n" +
                responseHeaders.toString() +
                "" + "\r\n" +
                responseBody.getContent();
    }
}
