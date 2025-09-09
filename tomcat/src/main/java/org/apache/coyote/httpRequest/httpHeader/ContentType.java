package org.apache.coyote.httpRequest.httpHeader;

import java.util.Arrays;

public enum ContentType {

    APPLICATION_JSON("application/json"),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded");

    private final String contentType;

    ContentType(final String contentType) {
        this.contentType = contentType;
    }

    public static ContentType findContentType(final String input) {
        return Arrays.stream(ContentType.values())
                .filter(type -> input.contains(type.contentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않은 형식입니다."));
    }
}
