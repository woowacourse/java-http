package org.apache.coyote.util;

public enum HttpContentType {

    HTML("text/html"),
    TEXT("text/plain"),
    CSS("text/css"),
    JS("text/javascript"),
    JSON("application/json"),
    ;

    private final String mediaType;

    HttpContentType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String mediaType() {
        return mediaType;
    }
}
