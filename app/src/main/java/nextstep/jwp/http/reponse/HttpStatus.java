package nextstep.jwp.http.reponse;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found");

    private final long code;
    private final String message;

    HttpStatus(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
