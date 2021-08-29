package nextstep.joanne.http;

public enum HttpStatus {
    OK(200, "OK"),

    FOUND(302, "Found"),

    UNAUTHORIZED(401, "Unauthorized");

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
