package org.apache.coyote.http11.response;

import java.util.Map;
import org.apache.coyote.http11.HttpHeaders;

public class HttpResponse {

    private HttpStatus status;
    private HttpHeaders headers;
    private byte[] body;

    public HttpResponse() {
        this(HttpStatus.OK, new HttpHeaders(Map.of()), new byte[0]);
    }

    public HttpResponse(final HttpStatus status, final HttpHeaders headers, final byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body.clone();
    }

    public HttpStatus status() {
        return status;
    }

    public HttpHeaders headers() {
        return headers.with("Content-Length", Integer.toString(body.length));
    }

    public byte[] body() {
        return body.clone();
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    public void setHeader(final String name, final String value) {
        this.headers = headers.with(name, value);
    }

    public void setBody(final byte[] body) {
        this.body = body.clone();
    }

    public void sendRedirect(final String location) {
        this.status = HttpStatus.FOUND;
        this.headers = headers.with("Location", location);
        this.body = new byte[0];
    }
}
