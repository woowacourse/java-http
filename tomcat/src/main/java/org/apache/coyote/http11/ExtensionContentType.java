package org.apache.coyote.http11;

public enum ExtensionContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("text/javascript");


    private String contentType;

    ExtensionContentType(String contentType) {
        this.contentType = contentType;
    }

    public static String toContentType(String extension) {
        return valueOf(extension.toUpperCase()).getContentType();
    }

    public String getContentType() {
        return contentType;
    }
}
