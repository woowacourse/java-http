package org.apache.coyote.http.vo;

import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpStatus;

public class HttpResponse {

    private static final String SUPPORT_HTTP_VERSION = "HTTP/1.1 ";
    private final HttpStatus status;
    private final HttpHeaders headers;
    private final String body;
    private final Cookie cookie;

    private HttpResponse(final HttpStatus status, final HttpHeaders headers, final String body, final Cookie cookie) {
        this.status = status;
        this.headers = headers;
        this.body = body;
        this.cookie = cookie;
        setContentLength();
    }

    private void setContentLength() {
        int size = body.getBytes().length;
        if (size > 0) {
            headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(size));
        }
    }

    public String getRawResponse() {
        if(cookie.getSize() != 0){
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

    public static class Builder {
        private HttpStatus status;
        private HttpHeaders headers;
        private String body = "";
        private Cookie cookie = Cookie.emptyCookie();

        public Builder status(final HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder headers(final HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public Builder cookie(final Cookie cookie) {
            this.cookie = cookie;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(status, headers, body, cookie);
        }
    }
}
