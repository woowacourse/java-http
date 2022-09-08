package org.apache.coyote.http11.message.response;

import lombok.Getter;

@Getter
public enum HttpStatus {
    OK(200),
    CREATED(201),
    NO_CONTENT(204),
    MOVED_PERMANENTLY(301),
    FOUND(302),
    NOT_MODIFIED(304),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    HttpStatus(final int code) {
        this.code = code;
    }

    public String getCodeAndPhrase() {
        return code + " " + name();
    }
}
