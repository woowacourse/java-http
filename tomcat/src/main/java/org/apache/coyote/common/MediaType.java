package org.apache.coyote.common;

import java.util.Arrays;

public enum MediaType {

    TEXT_HTML("text/html", ".html"),
    TEXT_CSS("text/css", ".css"),
    APPLICATION_JAVASCRIPT("application/javascript", ".js");

    private final String value;
    private final String type;

    MediaType(final String value, final String type) {
        this.value = value;
        this.type = type;
    }

    public static MediaType from(final String resourceUrl) {
        return Arrays.stream(MediaType.values())
                .filter(mediaType -> resourceUrl.endsWith(mediaType.type))
                .findFirst()
                .orElse(TEXT_HTML);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
