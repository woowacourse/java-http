package org.apache.coyote.http11.response;

import java.util.List;
import java.util.stream.Stream;

public enum ContentType {

    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JS("application/x-javascript", "js"),
    SVG("image/svg+xml", "svg"),
    ;

    private static final String FIELD_DELIMITER = ";";
    private static final String DEFAULT_CHARSET = "charset=utf-8";

    private final String type;
    private final List<String> extensions;

    ContentType(final String type, final String... extensions) {
        this.type = type;
        this.extensions = List.of(extensions);
    }

    public static ContentType from(final String fileName) {
        return Stream.of(values())
                .filter(contentType -> contentType.hasExtension(fileName))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    private boolean hasExtension(final String fileName) {
        return extensions.stream()
                .anyMatch(extension -> fileName.endsWith("." + extension));
    }

    public static String concat(final ContentType contentType) {
        return concat(contentType, DEFAULT_CHARSET);
    }

    private static String concat(final ContentType contentType, final String field) {
        return String.join(FIELD_DELIMITER, contentType.type, field);
    }
}
