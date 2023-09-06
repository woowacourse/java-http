package nextstep.jwp.http;

public enum HttpStatus {
    OK("200", "OK"),
    FOUND("302", "FOUND");

    private final String code;
    private final String text;

    HttpStatus(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
