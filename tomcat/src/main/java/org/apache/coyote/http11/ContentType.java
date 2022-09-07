package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html; charset=utf-8"),
    CSS("css", "text/css; charset=utf-8"),
    JS("js", "text/javascript; charset=utf-8"),
    ICO("ico", "image/x-icon"),
    SVG("svg", "image/svg+xml");

    private final String fileExtension;
    private final String value;

    ContentType(final String fileExtension, final String value) {
        this.fileExtension = fileExtension;
        this.value = value;
    }

    public static ContentType of(final String fileExtension) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.fileExtension.equals(fileExtension))
                .findFirst()
                .orElseGet(ContentType::getDefaultContentType);
    }

    private static ContentType getDefaultContentType() {
        return HTML;
    }

    public String getValue() {
        return value;
    }
}

