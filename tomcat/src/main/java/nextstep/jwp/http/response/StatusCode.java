package nextstep.jwp.http.response;

public enum StatusCode {

    OK("200", "OK");

    private final String code;
    private final String message;

    StatusCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
