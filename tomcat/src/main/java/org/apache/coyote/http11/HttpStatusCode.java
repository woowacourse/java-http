package org.apache.coyote.http11;

public enum HttpStatusCode {
    HTTP_STATUS_OK(200, "OK"),
    HTTP_STATUS_FOUND(302, "Found"),
    HTTP_STATUS_UNAUTHORIZED(401, "Unauthorized"),
    HTTP_STATUS_NOT_FOUND(404, "Not Found");

    private final int value;
    private final String description;

    HttpStatusCode(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public String toString() {
        return value + " " + description;
    }
}
