package org.apache.http;

import java.util.List;

public enum ContentType {
    TEXT_HTML("text/html;charset=utf-8", "html"),
    TEXT_CSS("text/css;charset=utf-8", "css"),
    ALL_ALL("application/octet-stream", "");

    private final String value;
    private final String extension;

    ContentType(String value, String extension) {
        this.value = value;
        this.extension = extension;
    }

    public static ContentType parse(String extension) {
        return List.of(ContentType.values()).stream()
                .filter(contentType -> contentType.getExtension().equals(extension))
                .findAny()
                .orElse(ALL_ALL);
    }

    public String getValue() {
        return value;
    }

    public String getExtension() {
        return extension;
    }
}
