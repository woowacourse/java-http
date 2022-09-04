package org.apache.coyote.http11.response.spec;

import java.util.Arrays;

public enum MimeType {

    HTML("text/html"),
    CSS("text/css"),
    JS("text/javascript"),
    SVG("image/svg+xml")
    ;

    private final String value;

    MimeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MimeType of(String type) {
        return Arrays.stream(values())
                .filter(mimeType -> mimeType.name().equalsIgnoreCase(type))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 mime type = " + type));
    }
}
