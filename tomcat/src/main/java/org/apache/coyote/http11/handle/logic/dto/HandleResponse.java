package org.apache.coyote.http11.handle.logic.dto;

import org.apache.coyote.http11.response.HttpStatus;

import java.util.Map;

public class HandleResponse {
    private final HttpStatus httpStatus;
    private final Map<String, String> body;

    public HandleResponse(final HttpStatus httpStatus, final Map<String, String> body) {
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
