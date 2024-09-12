package org.apache.catalina.http;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("text/javascript"),
    PLAIN("text/plain"),
    ;

    public static final String COMMA = ",";
    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
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
}
