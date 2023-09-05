package org.apache.coyote.common;

import java.util.Arrays;

public enum MediaType {

    TEXT_HTML("text/html", ".html"),
    TEXT_CSS("text/css", ".css"),
    APPLICATION_JAVASCRIPT("application/javascript", ".js");

    private final String source;
    private final String type;

    MediaType(final String source, final String type) {
        this.source = source;
        this.type = type;
    }

    public static MediaType from(final String resourceUrl) {
        return Arrays.stream(MediaType.values())
                .filter(mediaType -> resourceUrl.endsWith(mediaType.type))
                .findFirst()
                .orElse(TEXT_HTML);
    }

    public String source() {
        return source;
    }

    @Override
    public String toString() {
        return source;
    }
}
