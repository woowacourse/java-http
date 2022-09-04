package org.apache.coyote.http11;

import org.apache.coyote.http11.httpmessage.response.HttpStatus;

public class HandlerResponse {

    private final HttpStatus httpStatus;
    private final String body;

    public HandlerResponse(HttpStatus httpStatus, String body) {
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getBody() {
        return body;
    }
}
