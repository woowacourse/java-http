package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    SVG("svg", "image/svg+xml");

    private final String extension;
    private final String contentType;

    ContentType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static ContentType from(List<String> acceptTypes) {
        return Arrays.stream(values())
                .filter(contentType -> acceptTypes.stream()
                        .anyMatch(acceptType -> acceptType.contains(contentType.contentType)))
                .findAny()
                .orElseGet(() -> HTML);
    }

    @Override
    public String toString() {
        return this.contentType;
    }
}
