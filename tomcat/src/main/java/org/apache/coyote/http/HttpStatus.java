package org.apache.coyote.http;

public enum HttpStatus {
    OK("OK", 200),
    FOUND("FOUND", 302),
    UNAUTHORIZED("UNAUTHORIZED", 401);

    private final String codeName;
    private final int code;

    HttpStatus(final String codeName, final int code) {
        this.codeName = codeName;
        this.code = code;
    }

    public String getCodeName() {
        return codeName;
    }

    public int getCode() {
        return code;
    }
}

