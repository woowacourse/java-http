package org.apache.coyote.http11;

public enum MimeType {

    CSS("css", "text/css"),
    HTML("html", "text/html"),
    JSON("json", "application/json"),
    JAVASCRIPT("js", "application/javascript");

    private final String extension;
    private final String type;

    MimeType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public String getExtension() {
        return extension;
    }

    public String getType() {
        return type;
    }
}
