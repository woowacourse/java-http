package org.apache.coyote.http11.handler.ApiHandler;

import org.apache.coyote.http11.httpmessage.response.HttpStatus;

public class ApiHandlerResponse {

    private final HttpStatus httpStatus;
    private final Object body;

    public ApiHandlerResponse(HttpStatus httpStatus, Object body) {
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Object getBody() {
        return body;
    }
}
