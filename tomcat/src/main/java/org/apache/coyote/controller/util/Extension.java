package org.apache.coyote.controller.util;

import java.util.Arrays;

public enum Extension {

    CSS(".css", "text/css;charset=utf-8"),
    JS(".js", "application/json"),
    HTML(".html", "text/html;charset=utf-8"),
    INDEX("/", "text/html;charset=utf-8"),
    ICO(".ico", ""),
    ;

    private final String value;
    private final String contentType;

    Extension(final String value, final String contentType) {
        this.value = value;
        this.contentType = contentType;
    }

    public static Extension findExtension(final String uri) {
        return Arrays.stream(Extension.values())
                     .filter(extension -> uri.contains(extension.value))
                     .findAny()
                     .orElseThrow(() -> new IllegalArgumentException("잘못된 확장자입니다. " + uri));
    }

    public String getContentType() {
        return contentType;
    }
}
