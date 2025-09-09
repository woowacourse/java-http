package org.apache.coyote.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentType {

    // Text
    HTML("text/html", StandardCharsets.UTF_8),
    CSS("text/css", StandardCharsets.UTF_8),
    JAVASCRIPT("application/javascript", StandardCharsets.UTF_8),
    FORM_URLENCODED("application/x-www-form-urlencoded", StandardCharsets.UTF_8),
    ;

    public static final String HEADER_NAME = "content-type";
    public static final String HTML_EXTENSION = ".html";
    public static final String CSS_EXTENSION = ".css";
    public static final String JS_EXTENSION = ".js";

    private final String mimeType;
    private final Charset defaultCharset;

    public static ContentType from(final String path) {
        final String lowerPath = path.toLowerCase();

        if (lowerPath.endsWith(CSS_EXTENSION)) {
            return CSS;
        }

        if (lowerPath.endsWith(JS_EXTENSION)) {
            return JAVASCRIPT;
        }

        return getDefault();
    }

    public static ContentType fromHeader(final String contentTypeHeader) {
        if (contentTypeHeader == null || contentTypeHeader.isEmpty()) {
            return HTML;
        }

        final String mimeType = contentTypeHeader.split(";")[0].trim().toLowerCase();

        for (final ContentType type : ContentType.values()) {
            if (type.getMimeType().equalsIgnoreCase(mimeType)) {
                return type;
            }
        }

        return getDefault();
    }

    public static ContentType getDefault() {
        return HTML;
    }
}
