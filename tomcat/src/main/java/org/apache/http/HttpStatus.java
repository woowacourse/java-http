package org.apache.http;

public enum HttpStatus {
    OK("OK", 200),
    FOUND("FOUND", 302),
    UNAUTHORIZED("UNAUTHORIZED", 401),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", 500);

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

