package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpHeaders;

public record HttpResponse(HttpStatus status, HttpHeaders headers, byte[] body) {

    public HttpResponse {
        body = body.clone();
        headers = headers.with("Content-Length", Integer.toString(body.length));
    }

    public byte[] body() {
        return body.clone();
    }
}
