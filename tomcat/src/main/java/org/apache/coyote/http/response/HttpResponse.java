package org.apache.coyote.http.response;

import static org.apache.coyote.util.Constants.CRLF;

public class HttpResponse {

    private static final String BASIC_RESPONSE_BODY = "Hello world!";

    private final StatusLine statusLine;
    private final ResponseHeader headers;
    private final String body;

    public HttpResponse(StatusLine statusLine, ResponseHeader headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public void setRedirectLocation(String destination) {
        if (statusLine.needRedirectLocation()) {
            headers.setLocation(destination);
        }
    }

    public void setCookie(String cookie) {
        headers.setCookie(cookie);
    }

    public String toResponse() {
        return String.join(CRLF,
                statusLine.toResponse() + headers.toResponse(),
                "", body);
    }

    public static HttpResponse basicResponse() {
        return new HttpResponse(
                new StatusLine(HttpStatus.OK),
                ResponseHeader.basicResponseHeader(BASIC_RESPONSE_BODY.getBytes().length),
                BASIC_RESPONSE_BODY
        );
    }

    public static HttpResponse notFoundResponses() {
        return new HttpResponse(
                new StatusLine(HttpStatus.NOT_FOUND),
                ResponseHeader.basicResponseHeader(0),
                ""
        );
    }

    public static HttpResponse serverErrorResponses() {
        return new HttpResponse(
                new StatusLine(HttpStatus.INTERNAL_SERVER_ERROR),
                ResponseHeader.basicResponseHeader(0),
                ""
        );
    }
}
