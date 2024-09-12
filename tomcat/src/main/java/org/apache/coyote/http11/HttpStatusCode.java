package org.apache.coyote.http11;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "Found");

    private final int code;
    private final String displayName;

    HttpStatusCode(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return String.valueOf(code);
    }

    public String getDisplayName() {
        return displayName;
    }
}
