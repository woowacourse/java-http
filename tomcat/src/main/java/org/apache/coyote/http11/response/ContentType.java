package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "application/js"),
    ICO("ico", "image/vnd.microsoft.icon"),
    SVG("svg", "image/svg+xml");

    private final String fileExtension;
    private final String contentType;

    ContentType(final String fileExtension, final String contentType) {
        this.fileExtension = fileExtension;
        this.contentType = contentType;
    }

    public static ContentType from(final String fileExtension) {
        return Arrays.stream(values())
                .filter(m -> m.fileExtension.equals(fileExtension))
                .findFirst()
                .orElseThrow();
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getContentType() {
        return contentType;
    }
}
