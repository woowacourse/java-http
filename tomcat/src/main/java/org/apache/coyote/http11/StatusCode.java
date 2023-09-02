package org.apache.coyote.http11;

public enum StatusCode {
    OK("200 OK");

    private final String response;

    StatusCode(final String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
