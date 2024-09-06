package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum ContentType {
    ANY(List.of("*/*")),
    TEXT(List.of("text/plain")),
    JSON(List.of("application/json", "text/json")),
    JAVASCRIPT(List.of("application/javascript", "text/javascript")),
    CSS(List.of("text/css")),
    HTML(List.of("text/html")),
    URLENC(List.of("application/x-www-form-urlencoded"));

    private final List<String> mimeTypes;

    ContentType(List<String> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public static Optional<ContentType> fromMimeType(String mimeType) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.mimeTypes.contains(mimeType))
                .findFirst();
    }
}
