package org.apache.catalina.resource;

import java.util.Arrays;

public enum MimeType {

    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css;charset=utf-8"),
    JS("js", "application/javascript;charset=utf-8"),
    SVG("svg", "image/svg+xml"),
    PNG("png", "image/png"),
    ICO("ico", "image/x-icon"),
    DEFAULT("", "application/octet-stream");

    private final String extension;
    private final String value;

    MimeType(String extension, String value) {
        this.extension = extension;
        this.value = value;
    }

    public static MimeType fromPath(String path) {
        final int dot = path.lastIndexOf('.');
        if (dot == -1) {
            return DEFAULT;
        }
        final String ext = path.substring(dot + 1);
        return Arrays.stream(values())
                .filter(type -> type.extension.equalsIgnoreCase(ext))
                .findFirst()
                .orElse(DEFAULT);
    }

    public String extension() {
        return extension;
    }

    public String value() {
        return value;
    }
}
