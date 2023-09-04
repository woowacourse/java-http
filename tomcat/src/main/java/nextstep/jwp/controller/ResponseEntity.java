package nextstep.jwp.controller;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpResponseStatusLine;

public class ResponseEntity {

    private final HttpResponseStatusLine responseLine;
    private final HttpHeaders headers;
    private final String body;

    public ResponseEntity(final HttpResponseStatusLine responseLine, final HttpHeaders headers, final String body) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.body = body;
    }

    public ResponseEntity(final String contentType, final String body) {
        this(HttpResponseStatusLine.OK(), HttpHeaders.makeHttpResponseHeaders(contentType, body), body);
    }

    public HttpResponseStatusLine getResponseLine() {
        return responseLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
