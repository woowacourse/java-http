package org.apache.coyote.http11;

import org.apache.catalina.util.ResourceFile;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.header.HttpHeaderName;

public class HttpResponse {

    private static final String PROTOCOL = "HTTP/1.1 ";
    private static final String CRLF = "\r\n";

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
            return String.join(CRLF, statusLine, httpHeader.toHttpHeader(), "");
        }
        return String.join(CRLF, statusLine, httpHeader.toHttpHeader(), "", body);
    }

    public void setBody(ResourceFile responseBody) {
        httpHeader.addHeader(HttpHeaderName.CONTENT_TYPE, responseBody.contentType() + ";charset=utf-8");
        httpHeader.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(responseBody.body().getBytes().length));
        this.body = responseBody.body();
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
