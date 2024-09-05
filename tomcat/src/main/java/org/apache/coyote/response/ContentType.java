package org.apache.coyote.response;

import java.util.Arrays;

public enum ContentType {

    TEXT_HTML(".html", "text/html"),
    TEXT_CSS(".css", "text/css"),
    IMAGE_SVGXML(".svg", "image/svg+xml"),
    TEXT_JAVASCRIPT(".js", "text/javascript"),
    ;

    private final String fileExtension;
    private final String name;

    ContentType(String fileExtension, String name) {
        this.fileExtension = fileExtension;
        this.name = name;
    }

    public static ContentType fromFileExtension(String fileExtension) {
        return Arrays.stream(values())
                .filter(contentType -> fileExtension.equals(contentType.fileExtension))
                .findFirst()
                .orElse(TEXT_HTML);
    }

    public String getName() {
        return name;
    }
}
