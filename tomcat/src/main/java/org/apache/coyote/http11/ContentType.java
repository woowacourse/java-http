package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("text/js");

    private final String rawContentType;

    public static ContentType[] fromResourceName(String resourceName) {
        String fileExtension = resourceName.split("\\.")[1];
        return Arrays.stream(values())
                .filter(contentType -> contentType.rawContentType.contains(fileExtension))
                .toArray(ContentType[]::new);
    }

    ContentType(String rawContentType) {
        this.rawContentType = rawContentType;
    }

    public boolean isSame(String rawContentType) {
        return this.rawContentType.equals(rawContentType);
    }

    public String getRawContentType() {
        return rawContentType;
    }
}
