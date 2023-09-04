package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum HttpExtensionType {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "text/javascript"),
    ICO(".ico", "image/svg+xml"),
    ;

    private final String extension;
    private final String contentType;

    HttpExtensionType(final String extension, final String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static HttpExtensionType from(final String file) {
        return Arrays.stream(values())
                .filter(type -> file.endsWith(type.extension))
                .findAny()
                .orElse(HTML);
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }
}
