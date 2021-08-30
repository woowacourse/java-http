package nextstep.jwp.util;

public enum HttpStatus {
    OK("200", "OK"),
    FOUND("302", "FOUND"),
    UNAUTHORIZED("401", "UNAUTHORIZED");

    private String code;
    private String method;

    HttpStatus(String code, String method) {
        this.code = code;
        this.method = method;
    }

    public String value() {
        return this.code;
    }

    public String method() {
        return this.method;
    }
}
