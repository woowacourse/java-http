package nextstep.jwp.http.response;

public enum Status {
    OK("200"),
    FOUND("302"),
    BAD_REQUEST("400"),
    NOT_FOUND("404"),
    UNAUTHORIZED("401");

    private final String code;

    Status(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
