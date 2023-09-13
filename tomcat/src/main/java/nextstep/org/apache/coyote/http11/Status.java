package nextstep.org.apache.coyote.http11;

public enum Status {

    OK("200", "OK"),
    FOUND("302", "Found"),
    NOT_FOUND("404", "Not Found"),
    NOT_IMPLEMENTED("501", "Not Implemented"),
    INTERNAL_SERVER_ERROR("500", "Internal Server Error");

    private final String code;
    private final String message;

    Status(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCodeMessage() {
        return this.code + " " + this.message;
    }
}
