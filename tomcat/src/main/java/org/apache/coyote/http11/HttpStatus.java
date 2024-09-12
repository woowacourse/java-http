package org.apache.coyote.http11;

public enum HttpStatus {

    OK("200 OK"),
    FOUND("302 FOUND"),
    NOT_FOUND("404 NOT FOUND"),
    INTERNATIONAL_SERVER_ERROR("500 INTERNATIONAL SERVER ERROR"),
    ;

    private final String httpStatusMessage;

    HttpStatus(String httpStatusMessage) {
        this.httpStatusMessage = httpStatusMessage;
    }

    public String getHttpStatusMessage() {
        return httpStatusMessage;
    }
}
