package org.apache.coyote.http11.message.response;

import org.apache.coyote.http11.message.HttpBody;
import org.apache.coyote.http11.message.HttpHeaders;

public class HttpResponse {
    private final HttpStatus status;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpResponse(HttpStatus status, HttpHeaders headers, HttpBody body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return status.getCode();
    }

    public String getReasonPhrase() {
        return status.getReasonPhrase();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpBody getBody() {
        return body;
    }
}
