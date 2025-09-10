package org.apache.coyote.http11.message;

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
