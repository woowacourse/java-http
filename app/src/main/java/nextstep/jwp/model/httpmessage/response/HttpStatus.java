package nextstep.jwp.model.httpmessage.response;

public enum HttpStatus {
    OK("200", "OK"),
    REDIRECT("302", "Redirect"),
    NOT_FOUND("404", "Not Found");

    private final String status;
    private final String message;

    HttpStatus(String status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        return status + " " + message;
    }
}
