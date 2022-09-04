package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {
    CSS(".css"),
    JAVASCRIPT(".js"),
    HTML(".html");

    private final String subtype;

    ContentType(String subtype) {
        this.subtype = subtype;
    }

    public static ContentType findContentType(String uri) {
        return Arrays.stream(values())
                .filter(contentType -> uri.endsWith(contentType.subtype))
                .findFirst()
                .orElse(ContentType.HTML);
    }
}
