package nextstep.jwp.framework.http;

public enum HttpStatus {
    OK(200, "OK"),
    NOT_FOUND(404, "NOT FOUND");

    private final int code;
    private final String reasonPhrase;

    HttpStatus(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public int getCode() {
        return code;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
