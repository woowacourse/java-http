package org.apache.coyote;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JAVASCRIPT("js", "text/javascript"),
    FAVICON("ico", "image/x-icon");

    private final String extension;
    private final String type;

    ContentType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static ContentType from(String extension) {
        return Arrays.stream(values())
                .filter(it -> it.getExtension().equals(extension))
                .findFirst()
                .orElse(HTML);
    }

    public String getExtension() {
        return extension;
    }
}
