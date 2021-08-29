package nextstep.jwp.model;

public enum HttpStatus {
    OK("200 OK"),
    REDIRECT_FOUND("302 FOUND"),
    NOT_FOUND("404 NOT FOUND");

    private String message;

    HttpStatus(String message) {
        this.message = message;
    }
}
