package org.apache.coyote.http11.message.response.header;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html", ".html"),
    JS("text/javascript", ".js"),
    CSS("text/css", ".css"),
    SVG("image/svg+xml", ".svg"),
    ICO("image/x-icon", ".ico"),
    ;

    private static final String CHARSET = "charset=utf-8";

    private final String value;
    private final String extension;

    ContentType(final String value, final String extension) {
        this.value = String.join(";", value, CHARSET);
        this.extension = extension;
    }

    public static ContentType of(final String path) {
        return Arrays.stream(values())
                .filter(contentType -> path.endsWith(contentType.extension))
                .findFirst()
                .orElse(HTML);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
