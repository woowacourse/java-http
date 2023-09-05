package nextstep.jwp.http;

public enum HttpStatus {
    OK("200 OK"),
    FOUND("302 FOUND");

    private final String value;

    HttpStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
