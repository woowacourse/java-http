package org.apache.coyote.header;

public enum StatusCode {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    REDIRECT(301, "Redirect");

    private final int code;
    private final String value;

    StatusCode(final int code, final String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.join(" ",
                Integer.toString(this.code),
                this.value);
    }
}
