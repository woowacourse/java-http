package org.apache.coyote.http11;

public enum HttpHeader {
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    ;

    private final String httpForm;

    HttpHeader(String httpForm) {
        this.httpForm = httpForm;
    }

    public String getHttpForm() {
        return httpForm;
    }
}
