package org.apache.coyote.response;

public enum ContentType {
    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JS("text/javascript", "js"),
    PLAIN("text/plain", "txt"),
    FORM_DATA("multipart/formed-data", "svg"),
    ICO("multipart/formed-data", "ico");

    private final String contentType;
    private final String extension;

    ContentType(final String contentType, final String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public String getContentType() {
        return contentType;
    }
}
