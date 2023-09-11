package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html;charset=utf-8", ".html"),
    CSS("text/css;charset=utf-8", ".css"),
    JS("text/javascript;charset=utf-8", ".js"),
    SVG("image/svg+xml", ".svg"),
    ;

    private final String value;
    private final String extension;

    ContentType(
            String value,
            String extension
    ) {
        this.value = value;
        this.extension = extension;
    }

    public static ContentType findByExtension(String path) {
        return Arrays.stream(values())
                .filter(contentType -> path.endsWith(contentType.getExtension()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("invalid extension"));
    }

    public static boolean isExistsExtension(String path) {
        return Arrays.stream(values())
                .map(ContentType::getExtension)
                .anyMatch(path::endsWith);
    }

    public String getValue() {
        return value;
    }

    public String getExtension() {
        return extension;
    }

}
