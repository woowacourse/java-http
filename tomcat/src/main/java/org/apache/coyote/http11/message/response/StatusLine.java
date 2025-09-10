package org.apache.coyote.http11.message.response;

import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.HttpRequest;

public record StatusLine(
        HttpStatus status,
        String path,
        String httpVersion
) {
    public StatusLine(HttpStatus httpStatus, HttpRequest httpRequest) {
        this(
                httpStatus,
                httpRequest.getPath(),
                httpRequest.getHttpVersion()
        );
    }
}
