package org.apache.coyote.http11;

public enum Http11Status {
    OK(200, "OK"),
    FOUND(302, "Found"),
    NOT_FOUND(404, "Not Found"),;

    private int status;
    private String reason;

    Http11Status(int status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public int getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public String toString() {
        return String.valueOf(status) + " " + reason;
    }
}
