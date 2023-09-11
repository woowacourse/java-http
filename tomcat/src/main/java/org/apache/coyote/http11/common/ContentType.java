package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css;"),
    JS("js", "text/javascript;"),
    ICO("ico", "image/svg+xml;"),
    SVG(".svg", "text/html; "),
    ;

    private final String extension;
    private final String contentType;

    ContentType(final String extension, final String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static ContentType valueOfContentType(String requestExtension) {
        return Arrays.stream(values())
                .filter(value -> value.extension.equals(requestExtension))
                .findAny()
                .orElse(HTML);
    }

    public String getContentType() {
        return contentType;
    }

}
