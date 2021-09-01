package nextstep.jwp.http;

public enum HttpStatus {
    OK("200", "Ok"),
    FOUND("302", "Found"),
    BAD_REQUEST("400", "Bad Request"),
    NOT_FOUND("404", "Not Found"),
    CREATED("201", "Created"),
    INTERNAL_SERVER_ERROR("500", "Internal Server Error"),
    UNAUTHORIZED("401", "Unauthorized");

    private final String code;
    private final String name;

    HttpStatus(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return code + ' ' + name + ' ';
    }
}
