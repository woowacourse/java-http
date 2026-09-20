package org.apache.coyote.http11.response.headers;

import java.util.Arrays;

public enum ContentType {

    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "text/javascript"),
    ICO(".ico", "image/x-icon"),
    ;

    private static final ContentType DEFAULT = HTML;

    private final String extension;
    private final String value;

    ContentType(String extension, String value) {
        this.extension = extension;
        this.value = value;
    }

    public static ContentType from(String path) {
        return Arrays.stream(values())
                .filter(contentType -> path.endsWith(contentType.extension))
                .findFirst()
                .orElse(DEFAULT);
    }

    public String getValue() {
        return value;
    }

}
