package org.apache.coyote.http;

import org.apache.coyote.http.response.Assemblable;

public enum StatusCode implements Assemblable {

    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500),
    ;

    private final int code;

    StatusCode(int code) {
        this.code = code;
    }

    @Override
    public void assemble(StringBuilder builder) {
        String message = name().replace("_", " ");
        builder.append("%d %s \r\n".formatted(code, message));
    }
}
