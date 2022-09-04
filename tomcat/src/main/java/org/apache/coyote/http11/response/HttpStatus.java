package org.apache.coyote.http11.response;

public enum HttpStatus implements Response {

    OK(200),
    NOT_FOUND(404);

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    @Override
    public String getAsString() {
        return code + " " + name();
    }
}
