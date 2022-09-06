package org.apache.coyote.http11.http;

public enum StatusCode {

    OK("200"),
    FOUND("302"),
    UNAUTHORIZED("401");

    private String number;

    StatusCode(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
}
