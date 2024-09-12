package org.apache.catalina.http;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html"),
    CSS("text/css"),
    JS("text/javascript"),
    PLAIN("text/plain"),
    ;

    private static final String DEFAULT_CHARSET = "charset=utf-8";
    public static final String COMMA = ",";
    private final String value;

    ContentType(String value) {
        this.value = value;
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

    @Override
    public String toString() {
        return value + ";" + DEFAULT_CHARSET;
    }
}
