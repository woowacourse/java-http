package org.apache.coyote.http11.handle.logic.dto;

import nextstep.jwp.controller.dto.Response;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.Map;

public class HandleResponse {
    private final HttpStatus httpStatus;
    private final Map<String, String> body;

    public HandleResponse(final HttpStatus httpStatus, final Map<String, String> body) {
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public static HandleResponse from(final Response response) {
        return new HandleResponse(response.getHttpStatus(), response.getResponseHeader());
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
