package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css"),
    JS("js", "application/js"),
    ICO("ico", "image/vnd.microsoft.icon"),
    SVG("svg", "image/svg+xml"),
    ;

    private final String fileExtension;
    private final String name;

    ContentType(String fileExtension, String name) {
        this.fileExtension = fileExtension;
        this.name = name;
    }

    public static ContentType parseContentType(String fileExtension) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.fileExtension.equals(fileExtension))
                .findFirst()
                .orElse(HTML);
    }

    public String getName() {
        return name;
    }
}
