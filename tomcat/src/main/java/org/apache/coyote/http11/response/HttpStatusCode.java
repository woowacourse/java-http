package org.apache.coyote.http11.response;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "Found");

    private final int code;
    private final String statusPhrase;

    HttpStatusCode(int code, String statusPhrase) {
        this.code = code;
        this.statusPhrase = statusPhrase;
    }

    public boolean is2xxCode() {
        return code >= 200 && code < 300;
    }

    public boolean is3xxCode() {
        return code >= 300 && code < 400;
    }

    public int getCode() {
        return code;
    }

    public String getStatusPhrase() {
        return statusPhrase;
    }
}

