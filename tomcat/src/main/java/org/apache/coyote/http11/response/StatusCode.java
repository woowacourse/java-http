package org.apache.coyote.http11.response;

public enum StatusCode {

    OK("200");

    private final String code;

    StatusCode(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
