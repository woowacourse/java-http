package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum HttpContentType {
    TEXT_HTML("text/html", ".html"),
    CSS("text/css", ".css"),
    APPLICATION_JSON("application/json", ".json"),
    JAVASCRIPT("application/javascript", ".js"),
    FAVICON("image/x-icon", ".ico"),
    ;

    private static final String DEFAULT_UTF8 = "charset=utf-8";

    private final String contentType;
    private final String extension;

    HttpContentType(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public static HttpContentType from(final String fileName) {
        return Arrays.stream(values())
                .filter(value -> fileName.endsWith(value.extension))
                .findFirst()
                .orElseThrow();
    }

    public String message() {
        return String.format("%s;%s", contentType, DEFAULT_UTF8);
    }
}
