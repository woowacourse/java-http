package org.apache.coyote.http11;

import java.util.stream.Stream;

public enum ContentType {
    HTML("html", "text/html;charset=utf-8 "),
    CSS("css", "text/css "),
    JAVASCRIPT("js", "text/javascript "),
    FAVICON("ico", "image/x-icon ");

    private final String extension;
    private final String value;

    ContentType(String extension, String value) {
        this.extension = extension;
        this.value = value;
    }

    public static ContentType from(String url) {
        return Stream.of(values())
                .filter(contentType -> url.endsWith(contentType.extension))
                .findFirst()
                .orElse(HTML);
    }

    public static String getDefaultExtension() {
        return String.format(".%s", HTML.extension);
    }

    public String getValue() {
        return value;
    }
}
