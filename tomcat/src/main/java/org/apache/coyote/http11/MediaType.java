package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum MediaType {
    ANY(List.of("*/*")),
    TEXT(List.of("text/plain")),
    JSON(List.of("application/json", "text/json")),
    JAVASCRIPT(List.of("application/javascript", "text/javascript")),
    CSS(List.of("text/css")),
    HTML(List.of("text/html")),
    URLENC(List.of("application/x-www-form-urlencoded"));

    private final List<String> typeNames;

    MediaType(List<String> typeNames) {
        this.typeNames = typeNames;
    }

    public static Optional<MediaType> from(String typeName) {
        return Arrays.stream(MediaType.values())
                .filter(contentType -> contentType.typeNames.contains(typeName))
                .findFirst();
    }

    public static Optional<MediaType> findByPostfix(String postfix) {
        return Arrays.stream(MediaType.values())
                .filter(contentType -> contentType.existByPostfix(postfix))
                .findFirst();
    }

    private boolean existByPostfix(String postfix) {
        return typeNames.stream().anyMatch(name -> name.endsWith(postfix));
    }

    public String getTypeName() {
        return typeNames.get(0);
    }
}
