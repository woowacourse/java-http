package org.apache.coyote.http.vo;

import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpStatus;

public class HttpResponse {

    private static final String SUPPORT_HTTP_VERSION = "HTTP/1.1 ";

    private HttpHeaders headers;
    private Cookie cookie;
    private HttpStatus status;
    private String body;

    public HttpResponse() {
        this.headers = HttpHeaders.getEmptyHeaders();
        this.cookie = Cookie.emptyCookie();
    }

    public HttpResponse setStatus(final HttpStatus status) {
        this.status = status;
        return this;
    }

    public HttpResponse setHeader(final HttpHeader header, final String value) {
        this.headers.put(header, value);
        return this;
    }

    public HttpResponse setCookie(final Cookie cookie) {
        this.cookie = cookie;
        return this;
    }

    public HttpResponse setBody(final String body) {
        this.body = body;
        setContentLength();
        return this;
    }

    private void setContentLength() {
        int size = body.getBytes().length;
        if (size > 0) {
            headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(size));
        }
    }

    public String getRawResponse() {
        if (cookie.isPresent()) {
            return String.join(System.lineSeparator(),
                    SUPPORT_HTTP_VERSION + status.getStatus(),
                    headers.getRawHeaders(),
                    HttpHeader.SET_COOKIE.getKey() + ": " + cookie.toRawCookie(),
                    System.lineSeparator() + body
            );
        }

        return String.join(System.lineSeparator(),
                SUPPORT_HTTP_VERSION + status.getStatus(),
                headers.getRawHeaders(),
                System.lineSeparator() + body
        );
    }
}
