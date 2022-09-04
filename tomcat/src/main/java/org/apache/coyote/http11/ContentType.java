package org.apache.coyote.http11;

import java.util.Optional;

public enum ContentType {
    TEXT_HTML("text/html"), TEXT_JS("text/javascript"), TEXT_CSS("text/css");

    private final String value;

    public static Optional<ContentType> getByAcceptHeader(final String value) {
        if (value.equals("text/css")) {
            return Optional.of(TEXT_CSS);
        }

        if (value.equals("text/javascript")) {
            return Optional.of(TEXT_JS);
        }

        if (value.equals("text/html")) {
            return Optional.of(TEXT_HTML);
        }

        return Optional.empty();
    }

    public static Optional<ContentType> getByExtension(String extension) {
        if (extension.equals("js")) {
            return Optional.of(TEXT_JS);
        }

        if (extension.equals("css")) {
            return Optional.of(TEXT_CSS);
        }

        if (extension.equals("html")) {
            return Optional.of(TEXT_HTML);
        }

        return Optional.empty();
    }

    ContentType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
