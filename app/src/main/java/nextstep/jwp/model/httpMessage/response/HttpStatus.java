package nextstep.jwp.model.httpMessage.response;

public enum HttpStatus {
    OK("200", "OK"),
    REDIRECT("302", "Found");

    private final String httpStatus;
    private final String message;

    HttpStatus(String httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String toString() {
        return httpStatus + " " + message;
    }
}
