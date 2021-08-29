package nextstep.jwp.web.network.response;

public enum HttpStatus {

    OK(200),
    FOUND(302),
    UNAUTHORIZED(401);

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
