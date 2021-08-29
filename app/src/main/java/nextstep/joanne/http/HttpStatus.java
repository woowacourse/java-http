package nextstep.joanne.http;

public enum HttpStatus {
    OK(200, "OK"),

    FOUND(302, "Found"),

    UNAUTHORIZED(401, "Unauthorized"),

    NOT_FOUND(404, "Not Found"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ;

    private final int value;
    private final String responsePhrase;

    HttpStatus(int value, String responsePhrase) {
        this.value = value;
        this.responsePhrase = responsePhrase;
    }

    public int value() {
        return value;
    }

    public String responsePhrase() {
        return responsePhrase;
    }
}
