package org.apache.coyote;

import nextstep.jwp.controller.dto.Response;
import org.apache.coyote.http11.request.header.HttpHeadersLine;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.Map;

public class ResponseEntity {

    private final HttpStatus statusCode;
    private final HttpHeadersLine headers;
    private final String body;
    private final boolean isRestResponse;

    public ResponseEntity(HttpStatus statusCode, HttpHeadersLine headers, String body, boolean isRestResponse) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
        this.isRestResponse = isRestResponse;
    }

    public static ResponseEntity forward(HttpStatus statusCode, String path) {
        return new ResponseEntity(statusCode, HttpHeadersLine.initHeaders(), path, false);
    }

    public static ResponseEntity found(final Response response, String location) {
        final HttpHeadersLine headers = HttpHeadersLine.initHeaders();
        response.getResponseHeader().keySet().forEach(key -> headers.put(key, response.get(key)));
        headers.put("Location", location);
        return new ResponseEntity(HttpStatus.FOUND, headers, "", true);
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers.getHeaders();
    }

    public String getBody() {
        return body;
    }

    public boolean isRestResponse() {
        return isRestResponse;
    }
}
