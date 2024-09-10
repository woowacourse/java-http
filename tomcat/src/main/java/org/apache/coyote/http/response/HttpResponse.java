package org.apache.coyote.http.response;

import static org.apache.coyote.util.Constants.CRLF;

public class HttpResponse {

    private static final String BASIC_RESPONSE_BODY = "Hello world!";
    private static final String EMPTY_BODY = "";

    private StatusLine statusLine;
    private ResponseHeader headers;
    private String body;

    public HttpResponse() {
        this.statusLine = new StatusLine(HttpStatus.OK);
        this.headers = new ResponseHeader();
        this.body = EMPTY_BODY;
    }

    public void setResponse(StatusLine statusLine, ResponseHeader headers, String body) {
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
                EMPTY_BODY, body);
    }

    public void basicResponse() {
        this.statusLine = new StatusLine(HttpStatus.OK);
        this.headers = ResponseHeader.valueOfLength(BASIC_RESPONSE_BODY.getBytes().length);
        this.body = BASIC_RESPONSE_BODY;
    }

    public void notFoundResponses() {
        this.statusLine = new StatusLine(HttpStatus.NOT_FOUND);
        this.headers = ResponseHeader.valueOfLength(EMPTY_BODY.getBytes().length);
        this.body = EMPTY_BODY;
    }

    public void serverErrorResponses() {
        this.statusLine = new StatusLine(HttpStatus.INTERNAL_SERVER_ERROR);
        this.headers = ResponseHeader.valueOfLength(EMPTY_BODY.getBytes().length);
        this.body = EMPTY_BODY;
    }
}
