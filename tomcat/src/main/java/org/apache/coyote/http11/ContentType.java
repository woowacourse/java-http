package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css");

    private final String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;
    }

    public static ContentType from(String firstValueAccept) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.getValue().equals(firstValueAccept))
                .findFirst()
                .orElse(HTML);
    }

    public String getValue() {
        return this.contentType;
    }
}
