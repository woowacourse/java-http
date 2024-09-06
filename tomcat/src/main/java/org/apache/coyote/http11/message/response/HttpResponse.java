package org.apache.coyote.http11.message.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.message.HttpHeaders;

public class HttpResponse {

    private final HttpStatus status;
    private final HttpHeaders headers;
    private final String body;

    public HttpResponse(HttpStatus status, HttpHeaders headers, String body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(HttpStatus status, String body) {
        HttpHeaders headers = new HttpHeaders(Map.of(
                "Content-Length", String.valueOf(body.getBytes().length)
        ));
        return new HttpResponse(status, headers, body);
    }

    public static HttpResponse from(HttpStatus status) {
        HttpHeaders headers = new HttpHeaders(new HashMap<>());
        return new HttpResponse(status, headers, "");
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public List<String> getHeaderLines() {
        return headers.toHeaderLines();
    }

    public void setHeader(String name, String field) {
        headers.setHeader(name, field);
    }
}
