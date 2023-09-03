package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {
    HTML("html"),
    CSS("css"),
    JS("js"),
    ICO("ico"),
    SVG("svg");

    private final String fileExtension;

    ContentType(final String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public static ContentType from(final String fileExtension) {
        return Arrays.stream(values())
                .filter(m -> m.fileExtension.equals(fileExtension))
                .findFirst()
                .orElseThrow();
    }
}
