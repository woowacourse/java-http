package nextstep.jwp.http.common;

public enum HttpStatus {

    OK(200, "OK"),
    UNAUTHORIZED(401, "Unauthorized")
    ;

    private final int code;
    private final String description;

    HttpStatus(final int code, final String description) {
        this.code = code;
        this.description = description;
    }
}
