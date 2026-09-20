package org.apache.coyote.http11;

public enum HttpStatusCode {
    HTTP_STATUS_200("200"),
    HTTP_STATUS_302("302"),
    HTTP_STATUS_401("401"),
    HTTP_STATUS_404("404");

    private final String value;

    HttpStatusCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
