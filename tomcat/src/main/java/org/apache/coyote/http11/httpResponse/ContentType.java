package org.apache.coyote.http11.httpResponse;

import java.util.Arrays;

public enum ContentType {

    CSS("text/css", ".css"),
    JS("text/javascript", ".js"),
    SVG("image/svg+xml", ".svg"),
    HTML("text/html", ".html"),
    ;

    private final String mimeType;
    private final String extension;

    ContentType(
            final String mimeType,
            final String extension
    ) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public static String getContentType(final String path) {
        return Arrays.stream(values())
                .filter(contentType -> path.endsWith(contentType.extension))
                .findFirst()
                .map(contentType -> contentType.mimeType)
                .orElse(HTML.mimeType);
    }
}
