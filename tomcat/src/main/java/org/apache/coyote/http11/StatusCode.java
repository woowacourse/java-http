package org.apache.coyote.http11;

public enum StatusCode {
    OK("200 OK"), NOT_FOUND("404 Not Found"), UNAUTHORIZED("401 Unauthorized"), FOUND("302 Found");

    private final String response;

    StatusCode(final String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
