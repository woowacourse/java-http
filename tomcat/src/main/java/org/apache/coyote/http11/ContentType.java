package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html;charset=utf-8", ".html"),
    CSS("text/css", ".css"),
    JAVASCRIPT("application/js", ".js"),
    SVG("image/svg+xml", ".svg")
    ;

    private final String value;
    private final String fileNameExtension;

    ContentType(String value, String fileNameExtension) {
        this.value = value;
        this.fileNameExtension = fileNameExtension;
    }

    public static ContentType from(String uri) {
        return Arrays.stream(values())
                .filter(contentType -> uri.endsWith(contentType.fileNameExtension))
                .findAny()
                .orElse(HTML);
    }

    public String value() {
        return this.value;
    }
}
