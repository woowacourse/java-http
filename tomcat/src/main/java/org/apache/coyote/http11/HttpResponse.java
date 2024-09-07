package org.apache.coyote.http11;

public class HttpResponse {

    private final HttpHeader httpHeader;
    private final HttpStatus httpStatus;
    private final String body;

    public HttpResponse(HttpHeader httpHeader, HttpStatus httpStatus, String body) {
        this.httpHeader = httpHeader;
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public HttpResponse(HttpHeader httpHeader, HttpStatus httpStatus) {
        this(httpHeader, httpStatus, null);
    }

    public String toHttpResponse() {
        String statusLine = "HTTP/1.1 " + httpStatus.toHttpHeader() + " ";

        if (body == null) {
            return String.join("\r\n", statusLine, httpHeader.toHttpHeader(), "");
        }
        return String.join("\r\n", statusLine, httpHeader.toHttpHeader(), "", body);
    }
}
