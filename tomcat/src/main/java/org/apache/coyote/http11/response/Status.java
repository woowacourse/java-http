package org.apache.coyote.http11.response;

public enum Status {
    OK("200", "OK"),
    NOT_FOUND("404", "Not Found"),
    REDIRECT("302", "Found"),
    INTERNET_SERVER_ERROR("500", "Internal Server Error");

    final String code;
    final String message;

    Status(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
