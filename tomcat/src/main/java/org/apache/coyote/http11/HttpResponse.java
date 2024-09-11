package org.apache.coyote.http11;

public class HttpResponse {

    private static final String PROTOCOL = "HTTP/1.1 ";

    private final HttpHeader httpHeader;
    private HttpStatus httpStatus;
    private String body;

    public HttpResponse(HttpHeader httpHeader, HttpStatus httpStatus, String body) {
        this.httpHeader = httpHeader;
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public HttpResponse() {
        this(new HttpHeader(), null, null);
    }

    public String toHttpResponse() {
        String statusLine = PROTOCOL + httpStatus.getValue() + " " + httpStatus.getReason() + " ";

        if (body == null) {
            return String.join("\r\n", statusLine, httpHeader.toHttpHeader(), "");
        }
        return String.join("\r\n", statusLine, httpHeader.toHttpHeader(), "", body);
    }

    public void setBody(String body, String contentType) {
        httpHeader.addHeader(HttpHeaderName.CONTENT_TYPE, contentType + ";charset=utf-8");
        httpHeader.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        this.body = body;
    }

    public void setRedirect(String redirectUrl) {
        httpHeader.addHeader(HttpHeaderName.LOCATION, redirectUrl);
        httpStatus = HttpStatus.FOUND;
    }

    public void setCookie(String cookieName, String cookieValue) {
        httpHeader.addHeader(
                HttpHeaderName.SET_COOKIE,
                cookieName + HttpCookie.COOKIE_VALUE_DELIMITER + cookieValue
        );
    }

    public void setStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
