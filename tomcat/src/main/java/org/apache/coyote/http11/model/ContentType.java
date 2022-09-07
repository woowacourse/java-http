package org.apache.coyote.http11.model;

import java.util.stream.Stream;

public enum ContentType {

    HTML("html", "text/html;charset=utf-8"),
    JS("js", "text/javascript;charset=utf-8"),
    CSS("css", "text/css;charset=utf-8"),
    ;

    private final String extension;
    private final String contentType;

    ContentType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static String findContentType(String uri) {
        String fileExtension = uri.split("\\.")[1];

        return Stream.of(ContentType.values())
                .filter(f -> f.extension.equals(fileExtension))
                .map(f -> f.contentType)
                .findFirst()
                .orElse(HTML.contentType);
    }

    public static boolean hasFileExtension(String url) {
        return Stream.of(ContentType.values())
                .anyMatch(f -> url.endsWith(f.extension));
    }

    public String getContentType() {
        return contentType;
    }
}
