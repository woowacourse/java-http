package nextstep.jwp.model;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String responsePhrase;

    HttpStatus(int code, String responsePhrase) {
        this.code = code;
        this.responsePhrase = responsePhrase;
    }

    public int value() {
        return code;
    }

    public String responsePhrase() {
        return responsePhrase;
    }
}
