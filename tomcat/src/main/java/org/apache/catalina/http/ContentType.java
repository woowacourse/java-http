package org.apache.catalina.http;

import java.util.Arrays;
import java.util.List;

public enum ContentType {

    HTML("text/html", ".html"),
    CSS("text/css", ".css"),
    JS("text/javascript", ".js"),
    PLAIN("text/plain", ".txt"),
    ;

    private static final String DEFAULT_CHARSET = "charset=utf-8";
    private static final String MEDIA_TYPE_CHARSET_DELIMITER = ";";
    public static final String CONTENT_TYPE_SEPARATOR = ",";
    public static final int CONTENT_TYPE_INDEX = 0;
    private final String value;
    private final String fileExtension;

    ContentType(String value, String fileExtension) {
        this.value = value;
        this.fileExtension = fileExtension;
    }

    public static ContentType of(String acceptHeader) {
        if (acceptHeader == null) {
            return ContentType.HTML;
        }
        List<String> types = List.of(acceptHeader.split(CONTENT_TYPE_SEPARATOR));
        String contentType = types.get(CONTENT_TYPE_INDEX).trim();

        return Arrays.stream(values()).filter(value -> value.value.equals(contentType))
                .findAny()
                .orElse(ContentType.HTML);
    }

    public static boolean isValidFileExtension(String fileName) {
        return Arrays.stream(values())
                .anyMatch(contentType -> fileName.endsWith(contentType.fileExtension));
    }

    @Override
    public String toString() {
        return value + MEDIA_TYPE_CHARSET_DELIMITER + DEFAULT_CHARSET;
    }
}
