package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum ResourceContentType {
    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css"),
    SCRIPT("js", "text/javascript"),
    ICON("ico", "image/x-icon"),
    SVG("svg", "image/svg+xml"),
    ;

    private static final String EXTENSION_FLAG = ".";

    private final String extension;
    private final String contentType;

    ResourceContentType(final String extension, final String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static ResourceContentType from(final String requestURI) {
        final String extensionType = requestURI.substring(requestURI.lastIndexOf(EXTENSION_FLAG) + 1);
        return Arrays.stream(values())
                .filter(value -> value.extension.equalsIgnoreCase(extensionType))
                .findFirst()
                .orElse(HTML);
    }

    public String getContentType() {
        return contentType;
    }
}
