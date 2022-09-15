package org.apache.http.info;

public enum StatusCode {

    OK_200("200 OK"),
    FOUND_302("302 Found"),
    UNAUTHORIZED_401("401 Unauthorized"),
    NOT_FOUND_404("404 Not Found"),
    INTERNAL_SERVER_ERROR_500("500 Internal Server Error"),
    ;

    private final String name;

    StatusCode(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
