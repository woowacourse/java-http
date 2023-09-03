package org.apache.coyote.http.vo;

import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpStatus;

public class HttpResponse {

    private static final String SUPPORT_HTTP_VERSION = "HTTP/1.1 ";
    private final HttpStatus status;
    private final HttpHeaders headers;
    private final String body;

    public HttpResponse(final HttpStatus status, final HttpHeaders headers) {
        this.status = status;
        this.headers = headers;
        this.body = null;
    }

    public HttpResponse(final HttpStatus status, final HttpHeaders headers, final String body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
        setContentType();
    }

    private void setContentType() {
        headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }

    public String getRawResponse() {
        return String.join("\r\n",
                SUPPORT_HTTP_VERSION + status.getStatus(),
                headers.getRawHeaders(),
                "\n" + body
        );
    }
}
