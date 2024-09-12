package org.apache.catalina.http;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html", ".html"),
    CSS("text/css", ".css"),
    JS("text/javascript", ".js"),
    PLAIN("text/plain", ".txt"),
    ;

    private static final String DEFAULT_CHARSET = "charset=utf-8";
    public static final String COMMA = ",";
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

        String[] types = acceptHeader.split(COMMA);
        String contentType = types[0].trim();

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
        return value + ";" + DEFAULT_CHARSET;
    }
}
