package org.apache.coyote.http11.http;

public enum StatusCode {

    OK("200", "OK"),
    FOUND("302", "FOUND"),
    UNAUTHORIZED("401", "UNAUTHORIZED");

    private String number;
    private String name;

    StatusCode(String number, String name) {
        this.number = number;
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
}
