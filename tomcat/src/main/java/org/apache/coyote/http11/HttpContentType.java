package org.apache.coyote.http11;

public enum HttpContentType {

    TEXT_HTML("text/html"),
    APPLICATION_JSON("application/json"),
    ;

    private final String contentType;

    HttpContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
