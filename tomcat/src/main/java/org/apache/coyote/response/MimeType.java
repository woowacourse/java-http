package org.apache.coyote.response;

import java.util.Arrays;

public enum MimeType {

    HTML("text/html"),
    CSS("text/css"),
    JS("application/javascript"),
    SVG("image/svg+xml"),
    ICO("image/x-icon"),
    ;

    private final String type;

    MimeType(String type) {
        this.type = type;
    }

    public static MimeType from(String fileName) {
        return Arrays.stream(values())
                .filter(mimeType -> fileName.endsWith(mimeType.name().toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown mime type for file: " + fileName));
    }

    public String getType() {
        return this.type;
    }
}
