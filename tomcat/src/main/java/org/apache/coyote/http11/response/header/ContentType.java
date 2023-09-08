package org.apache.coyote.http11.response.header;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html", ".html"),
    CSS("text/css", ".css"),
    JS("text/javascript", ".js"),
    ICO("image/x-icon", ".ico"),
    SVG("image/svg+xml", ".svg");

    private static final String CHARSET = "charset=utf-8";

    private final String content;
    private final String extension;

    ContentType(final String content, final String extension) {
        this.content = String.join(";", content, CHARSET);
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
        return this.content;
    }
}
