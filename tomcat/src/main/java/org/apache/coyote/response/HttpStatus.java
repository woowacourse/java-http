package org.apache.coyote.response;

public enum HttpStatus {

    OK("OK", 200);

    private final String statusName;
    private final int statusCode;

    HttpStatus(final String statusName, final int statusCode) {
        this.statusName = statusName;
        this.statusCode = statusCode;
    }

    public String statusName() {
        return statusName;
    }

    public int statusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return "HttpStatus{" +
               "statusName='" + statusName + '\'' +
               ", statusCode=" + statusCode +
               '}';
    }
}
