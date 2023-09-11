package org.apache.coyote.http11.response;

public enum ResponseHeaderKey {
    LOCATION("Location"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    SET_COOKIE("Set-Cookie"),
    JSESSION("JSESSIONID");

    private final String responseHeaderName;

    ResponseHeaderKey(String responseHeaderName) {
        this.responseHeaderName = responseHeaderName;
    }

    public String getResponseHeaderName() {
        return responseHeaderName;
    }
}
