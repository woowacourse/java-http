package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {
    TEXT_HTML("text/html", "html"),
    TEXT_CSS("text/css", "css"),
    TEXT_JS("text/js", "js"),
    IMAGE_CSV("image/svg+xml", "svg"),
    IMAGE_ICO("image/x-icon", "ico"),
    ;

    private final String name;
    private final String extension;

    ContentType(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }

    public static String from(String url) {
        int index = url.indexOf(".");
        if (index == -1) {
            return TEXT_HTML.name;
        }
        return Arrays.stream(values())
                .filter(contentType -> url.endsWith(contentType.extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알맞은 확장자가 없습니다."))
                .name;
    }
}
