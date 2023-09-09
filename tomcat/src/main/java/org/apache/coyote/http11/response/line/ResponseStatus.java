package org.apache.coyote.http11.response.line;

public enum ResponseStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    ;

    private final int code;
    private final String response;

    ResponseStatus(int code, String response) {
        this.code = code;
        this.response = response;
    }

    public String codeMessage() {
        return String.valueOf(code);
    }

    public String responseMessage() {
        return response;
    }
}
