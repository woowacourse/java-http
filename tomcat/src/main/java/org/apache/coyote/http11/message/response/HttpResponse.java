package org.apache.coyote.http11.message.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.message.HttpHeaders;

public class HttpResponse {

    private static final byte[] EMPTY_BODY = new byte[0];
    private HttpStatus status;
    private final HttpHeaders headers;
    private final byte[] body;

    public HttpResponse(HttpStatus status, HttpHeaders headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(HttpStatus status, byte[] body) {
        HttpHeaders headers = new HttpHeaders(Map.of(
                "Content-Length", String.valueOf(body.length)
        ));
        return new HttpResponse(status, headers, body);
    }

    public static HttpResponse from(HttpStatus status) {
        HttpHeaders headers = new HttpHeaders(new HashMap<>());
        return new HttpResponse(status, headers, EMPTY_BODY);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public byte[] getBody() {
        return body;
    }

    public List<String> getHeaderLines() {
        return headers.toHeaderLines();
    }

    public void setHeader(String name, String field) {
        headers.setHeader(name, field);
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
