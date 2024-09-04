package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    TEXT_HTML(".html", "text/html"),
    TEXT_JS(".js", "text/javascript"),
    TEXT_CSS(".css", "text/javascript"),
    ;

    private final String extension;
    private final String contentType;

    ContentType(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static String findContentType(String requestUrl) {
        return Arrays.stream(ContentType.values())
                .filter(contentType1 -> requestUrl.endsWith(contentType1.extension))
                .findAny()
                .orElse(TEXT_HTML)
                .contentType;
    }
}
