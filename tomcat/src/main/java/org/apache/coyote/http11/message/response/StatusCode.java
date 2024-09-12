package org.apache.coyote.http11.message.response;

public enum StatusCode {
    OK(200, "OK"),
    FOUND(302, "Found");

    private final int code;
    private final String displayName;

    StatusCode(int code, String displayName) {
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
