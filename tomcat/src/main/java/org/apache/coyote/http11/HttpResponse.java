package org.apache.coyote.http11;

import java.util.*;

public class HttpResponse {

    private final String body;
    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final Map<String, String> headers = new HashMap<>();

    public HttpResponse(String body, HttpStatus httpStatus, ContentType contentType) {
        this.body = body;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
    }

    public String getBody() {
        return body;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getContentType() {
        return contentType.getType();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return Map.copyOf(headers);
    }
}
