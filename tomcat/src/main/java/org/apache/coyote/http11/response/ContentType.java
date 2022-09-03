package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {

    DEFAULT("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/javascript"),
    CSV("svg", "image/svg+xml"),
    ICO("ico", "image/x-icon"),
    JSON("json", "Application/json"),
    ;

    private final String type;
    private final String name;

    ContentType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String find(String uri) {
        ContentType foundContentType = Arrays.stream(ContentType.values())
                .filter(contentType -> uri.endsWith(contentType.type))
                .findAny()
                .orElse(DEFAULT);

        return foundContentType.name;
    }
}
