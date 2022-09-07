package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css,*/*;q=0.1"),
    JS("js", "text/javascript; charset=utf-8"),
    SVG("svg", "image/svg+xml");

    private final String contentType;
    private final String contentTypeToString;

    ContentType(String contentType, String contentTypeToString) {
        this.contentType = contentType;
        this.contentTypeToString = contentTypeToString;
    }

    public static String findContentType(String uri) {
        return Arrays.stream(ContentType.values())
            .filter(c -> uri.contains(c.contentType))
            .findFirst()
            .orElse(HTML)
            .contentTypeToString;
    }
}
