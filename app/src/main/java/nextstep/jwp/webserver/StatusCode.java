package nextstep.jwp.webserver;

public enum StatusCode {
    _200_OK(200, "Ok"),
    _302_FOUND(302, "Found"),
    _401_UNAUTHORIZED(401, "Unauthorized"),
    _404_NOT_FOUND(404, "Not Found"),
    _500_INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String name;

    StatusCode(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getString() {
        return code + " " + name;
    }
}
