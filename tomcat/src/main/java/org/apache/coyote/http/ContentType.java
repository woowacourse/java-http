package org.apache.coyote.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {

    // Text
    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    JAVASCRIPT("application/javascript");

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
