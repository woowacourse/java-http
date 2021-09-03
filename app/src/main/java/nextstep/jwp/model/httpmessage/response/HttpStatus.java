package nextstep.jwp.model.httpmessage.response;

public enum HttpStatus {
    OK("200", "OK"),
    REDIRECT("302", "Redirect"),
    NOT_FOUND("404", "Not Found"),
    UNAUTHORIZED("401", "Unauthorized");

    private final String value;
    private final String message;

    HttpStatus(String value, String message) {
        this.value = value;
        this.message = message;
    }

    public String value() {
        return value;
    }

    public String getProtocol() {
        return "HTTP/1.1";
    }

    @Override
    public String toString() {
        return value + " " + message;
    }
}
