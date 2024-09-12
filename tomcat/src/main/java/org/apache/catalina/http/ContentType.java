package org.apache.catalina.http;

import java.util.Arrays;
import java.util.List;

public enum ContentType {

    HTML("text/html", "text/html", ".html"),
    CSS("text/css", "text/css", ".css"),
    JS("text/javascript", "text/javascript", ".js"),
    PLAIN("text/plain", "text/plain", ".txt"),
    SVG_IMAGE("image/avif", "image/svg+xml", "svg"),
    ;

    private static final String DEFAULT_CHARSET = "charset=utf-8";
    private static final String MEDIA_TYPE_CHARSET_DELIMITER = ";";
    public static final String CONTENT_TYPE_SEPARATOR = ",";
    public static final int CONTENT_TYPE_INDEX = 0;
    private final String accept;
    private final String type;
    private final String fileExtension;

    ContentType(String accept, String type, String fileExtension) {
        this.accept = accept;
        this.type = type;
        this.fileExtension = fileExtension;
    }

    public static ContentType of(String acceptHeader) {
        if (acceptHeader == null) {
            return ContentType.HTML;
        }
        List<String> types = List.of(acceptHeader.split(CONTENT_TYPE_SEPARATOR));
        String contentType = types.get(CONTENT_TYPE_INDEX).trim();

        return Arrays.stream(values()).filter(value -> value.accept.equals(contentType))
                .findAny()
                .orElse(ContentType.HTML);
    }

    public static boolean isValidFileExtension(String fileName) {
        return Arrays.stream(values())
                .anyMatch(contentType -> fileName.endsWith(contentType.fileExtension));
    }

    @Override
    public String toString() {
        return type + MEDIA_TYPE_CHARSET_DELIMITER + DEFAULT_CHARSET;
    }
}
