package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    TEXT_HTML(".html", "text/html;charset=utf-8 "),
    TEXT_CSS(".css", "text/css;charset=utf-8 "),
    APPLICATION_JAVASCRIPT(".js", "application/javascript "),
    IMAGE_X_ICO(".ico", "image/x-icon "),
    ;

    private final String extension;
    private final String mimeType;

    ContentType(final String extension, final String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public static ContentType getByRequestTarget(final String requestTarget) {
        return Arrays.stream(values())
            .filter(type -> requestTarget.endsWith(type.extension))
            .findFirst()
            .orElse(TEXT_HTML);
    }

    public String getMimeType() {
        return mimeType;
    }
}
