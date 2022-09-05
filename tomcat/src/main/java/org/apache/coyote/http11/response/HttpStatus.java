package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK("200 OK"),
    NOT_FOUND("404 NOT FOUND");

    final private String code;

    HttpStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
