package org.apache.coyote.response;

import java.util.StringJoiner;
import org.apache.coyote.cookie.Cookie;
import org.apache.coyote.cookie.Cookies;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private HttpStatus httpStatus = HttpStatus.OK;
    private String contentType;
    private String location;
    private Cookies cookies = Cookies.init();
    private String responseBody;

    public HttpResponse() {
    }

    public byte[] getResponse() {
        return createResponse().getBytes();
    }

    private String createResponse() {
        StringJoiner joiner = new StringJoiner(CRLF);
        joiner.add(httpStatus.createStatusLine());
        joiner.add(String.format("Content-Type: %s;charset=utf-8 ", contentType));

        addHeaders(joiner);
        joiner.add("");
        if (responseBody != null) {
            joiner.add(responseBody);
        }
        return joiner.toString();
    }

    private void addHeaders(final StringJoiner joiner) {
        if (responseBody != null) {
            joiner.add(String.format("Content-Length: " + responseBody.getBytes().length + " "));
        }
        if (location != null) {
            joiner.add(String.format("Location: %s", location));
        }
        if (!cookies.isEmpty()) {
            joiner.add(String.format("Set-Cookie: %s", cookies.toHeaders()));
        }
    }

    public HttpResponse setHttpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponse setContentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpResponse setLocation(final String location) {
        this.location = location;
        return this;
    }

    public HttpResponse setCookie(final Cookie cookie) {
        cookies.add(cookie);
        return this;
    }

    public HttpResponse setResponseBody(final String responseBody) {
        this.responseBody = responseBody;
        return this;
    }
}
