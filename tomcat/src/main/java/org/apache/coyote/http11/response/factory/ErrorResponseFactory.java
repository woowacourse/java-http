package org.apache.coyote.http11.response.factory;

import static org.apache.coyote.Constants.ROOT;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.element.HttpStatus;

public enum ErrorResponseFactory {
    NOT_FOUND(HttpResponse.from(HttpResponseBody.of(ROOT + "/404.html"), HttpStatus.NOT_FOUND,
            "text/html")),
    UNAUTHORIZED(HttpResponse.from(HttpResponseBody.of(ROOT + "/401.html"), HttpStatus.UNAUTHORIZED,
            "text/html")),
    INTERNAL_SERVER_ERROR(HttpResponse.from(HttpResponseBody.of(ROOT + "/500.html"), HttpStatus.INTERNAL_SERVER_ERROR,
            "text/html"));

    private final HttpResponse value;

    ErrorResponseFactory(HttpResponse value) {
        this.value = value;
    }

    public HttpResponse getValue() {
        return value;
    }
}
