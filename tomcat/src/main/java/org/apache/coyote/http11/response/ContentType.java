package org.apache.coyote.http11.response;

import java.util.Arrays;

enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("text/js");

    private final String rawContentType;

    static ContentType[] fromResourceName(String resourceName) {
        String fileExtension = resourceName.split("\\.")[1];
        return Arrays.stream(values())
                .filter(contentType -> contentType.rawContentType.contains(fileExtension))
                .toArray(ContentType[]::new);
    }

    ContentType(String rawContentType) {
        this.rawContentType = rawContentType;
    }

    public String getRawContentType() {
        return rawContentType;
    }
}
