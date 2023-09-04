package org.apache.coyote.http11.response;

import org.apache.coyote.http11.cookie.Cookie;

public class Response {
    private final StatusLine statusLine;
    private final ResponseHeader responseHeader;
    private final ResponseBody responseBody;

    private Response(final StatusLine statusLine, final ResponseHeader responseHeader, final ResponseBody responseBody) {
        this.statusLine = statusLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public static Response of(final HttpStatus httpStatus,
                              final ResponseBody responseBody) {
        return new Response(
                StatusLine.from(httpStatus),
                ResponseHeader.from(responseBody),
                responseBody
        );
    }

    public static Response redirect(final HttpStatus httpStatus,
                                    final String redirectPath,
                                    final ResponseBody responseBody) {
        return new Response(
                StatusLine.from(httpStatus),
                ResponseHeader.redirect(redirectPath),
                responseBody
        );
    }

    public void addSession(final String sessionId) {
        responseHeader.addCookie(new Cookie("JSESSIONID", sessionId));
    }

    @Override
    public String toString() {
        return statusLine.getStatusLine() + "\r\n" +
                responseHeader.toString() +
                "" + "\r\n" +
                responseBody.getContent();
    }
}
