package org.apache.coyote.http11.response;

public enum StatusCode {

    OK("200", "OK"),
    FOUND("302", "FOUND"),
    BAD_REQUEST("400", "BAD REQUEST"),
    UNAUTHORIZED("401", "UNAUTHORIZED"),
    NOT_FOUND("404", "NOT FOUND"),
    INTERNAL_SERVER_ERROR("500", "INTERNAL SERVER ERROR");

    private final String statusNumber;
    private final String statusName;

    StatusCode(final String statusNumber, final String statusName) {
        this.statusNumber = statusNumber;
        this.statusName = statusName;
    }

    public CharSequence statusCodeToString() {
        return String.format("%s %s", statusNumber, statusName);
    }
}
