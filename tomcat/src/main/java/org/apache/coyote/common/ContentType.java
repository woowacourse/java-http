package org.apache.coyote.common;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JAVASCRIPT("js", "text/javascript"),
    FAVICON("ico", "image/x-icon"),
    SVG("svg", "image/svg+xml");

    private final String extension;
    private final String type;

    ContentType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static ContentType from(String extension) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.getExtension().equals(extension))
                .findFirst()
                .orElse(HTML);
    }

    public String getExtension() {
        return extension;
    }

    public String getType() {
        return type;
    }
}
