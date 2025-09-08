package org.apache.coyote.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {

    // Text
    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    JAVASCRIPT("application/javascript"),
    FORM_URLENCODED("application/x-www-form-urlencoded"),
    ;

    public static final String HEADER_NAME = "content-type";

    private final String mimeType;

    public static ContentType from(final String path) {
        final String lowerPath = path.toLowerCase();

        if (lowerPath.endsWith(".css")) {
            return CSS;
        }

        if (lowerPath.endsWith(".js")) {
            return JAVASCRIPT;
        }

        return HTML;
    }
}
