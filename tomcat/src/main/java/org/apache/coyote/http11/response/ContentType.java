package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css"),
    JS("js", "application/js"),
    ICO("ico", "image/vnd.microsoft.icon"),
    SVG("svg", "image/svg+xml");

    private final String fileExtension;
    private final String type;

    ContentType(final String fileExtension, final String type) {
        this.fileExtension = fileExtension;
        this.type = type;
    }

    public static ContentType from(final String fileExtension) {
        return Arrays.stream(values())
                .filter(m -> m.fileExtension.equals(fileExtension))
                .findFirst()
                .orElseThrow();
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getType() {
        return type;
    }
}
