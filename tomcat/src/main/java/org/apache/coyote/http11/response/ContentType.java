package org.apache.coyote.http11.response;

public enum ContentType {
    CSS("text/css"),
    HTML("text/html"),
    JAVASCRIPT("text/javascript"),
    PLAIN("text/plain"),
    JSON("application/json"),
    ;

    private final String mimeType;

    ContentType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}
