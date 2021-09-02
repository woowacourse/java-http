package nextstep.jwp.framework.http.common;

public enum HttpStatus {

    OK(200, "OK"),
    CREATED(201, "Created"),

    FOUND(302, "Found"),

    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found");

    private final int value;
    private final String responsePhrase;

    HttpStatus(final int value, final String responsePhrase) {
        this.value = value;
        this.responsePhrase = responsePhrase;
    }

    public int value() {
        return value;
    }

    public String getReasonPhrase() {
        return responsePhrase;
    }
}
