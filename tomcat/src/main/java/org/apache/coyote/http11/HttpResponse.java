package org.apache.coyote.http11;

public class HttpResponse {

    private final HttpHeader httpHeader;
    private HttpStatus httpStatus;
    private String body;

    public HttpResponse(HttpHeader httpHeader, HttpStatus httpStatus, String body) {
        this.httpHeader = httpHeader;
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public HttpResponse(HttpHeader httpHeader, HttpStatus httpStatus) {
        this(httpHeader, httpStatus, null);
    }

    public HttpResponse() {
        this(new HttpHeader(), null, null);
    }

    public String toHttpResponse() {
        String statusLine = "HTTP/1.1 " + httpStatus.getValue() + " " + httpStatus.getReason() + " ";

        if (body == null) {
            return String.join("\r\n", statusLine, httpHeader.toHttpHeader(), "");
        }
        return String.join("\r\n", statusLine, httpHeader.toHttpHeader(), "", body);
    }

    public void setBody(String body, String contentType) {
        httpHeader.addHeader("Content-Type", contentType + ";charset=utf-8");
        httpHeader.addHeader("Content-Length", String.valueOf(body.getBytes().length));

        this.body = body;
    }

    public void setRedirect(String redirectUrl) {
        httpHeader.addHeader("Location", redirectUrl);
        httpStatus = HttpStatus.FOUND;
    }

    public void setCookie(String cookieName, String cookieValue) {
        httpHeader.addHeader("Set-Cookie", cookieName + "=" + cookieValue);
    }

    public void setStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
