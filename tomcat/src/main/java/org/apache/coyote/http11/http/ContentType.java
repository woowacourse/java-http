package org.apache.coyote.http11.http;

import java.util.Arrays;
import nextstep.jwp.exception.NotFoundException;

public enum ContentType {

    TEXT_HTML("text/html", "html"),
    TEXT_CSS("text/css", "css"),
    APPLICATION_JAVASCRIPT("application/javascript", "js"),
    ;

    private final String value;
    private final String extension;

    ContentType(final String value, final String extension) {
        this.value = value;
        this.extension = extension;
    }

    public static ContentType from(final String uri) {
        if (containsExtension(uri)) {
            String extension = uri.split("\\.")[1];
            return findByExtension(extension);
        }
        return TEXT_HTML;
    }

    private static boolean containsExtension(final String uri) {
        return uri.contains(".");
    }

    private static ContentType findByExtension(final String extension) {
        return Arrays.stream(values())
                .filter(value -> value.extension.equals(extension))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Content-Type not found"));
    }

    public String getValue() {
        return value;
    }

    public String getExtension() {
        return extension;
    }
}
