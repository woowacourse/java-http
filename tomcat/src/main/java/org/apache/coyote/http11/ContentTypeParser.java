package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentTypeParser {

    HTML("text/html"),
    CSS("text/css"),
    JS("text/javascript"),
    ICO("image/x-icon")
    ;

    private final String contentType;

    ContentTypeParser(String contentType) {
        this.contentType = contentType;
    }

    public static String parse(String path) {
        int commaLocation = path.lastIndexOf(".");
        String resourceExtension = path.substring(commaLocation + 1);

        return Arrays.stream(values())
                .filter(value -> value.name().toLowerCase().equals(resourceExtension))
                .findFirst()
                .orElse(HTML)
                .contentType;
    }
}
