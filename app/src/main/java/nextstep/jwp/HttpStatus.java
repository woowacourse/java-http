package nextstep.jwp;

public enum HttpStatus {

    OK(200),
    MULTIPLE_CHOICES(300),
    BAD_REQUEST(400),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int toCode() {
        return code;
    }

    public String toReasonPhrase() {
        return name();
    }
}
