package org.apache.coyote.http11;

import java.util.Arrays;

public enum MediaType {

    HTML("text/html;charset=utf-8", "html"),
    JAVASCRIPT("application/javascript", "js"),
    CSS("text/css", "css")
    ;

    private final String value;
    private final String extension;

    MediaType(final String value, final String extension) {
        this.value = value;
        this.extension = extension;
    }

    public static MediaType of(String uri) {
        return Arrays.stream(values())
                .filter(mediaType -> uri.endsWith(mediaType.extension))
                .findFirst()
                .orElse(HTML);
    }

    public String getValue() {
        return value;
    }
}
