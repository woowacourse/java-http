package org.apache.coyote.http11;

import java.util.Arrays;
import nextstep.jwp.exception.NotAllowedMethodException;

public enum ExtensionType {

    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css"),
    JS(".js", "text/javascript"),
    ICO(".ico", "image/svg+xml"),
    SVG(".svg", "image/svg+xml");

    private final String extension;
    private final String contentType;

    ExtensionType(final String extension, final String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static ExtensionType from(final String extension) {
        return Arrays.stream(values())
                .filter(it -> extension.contains(it.extension))
                .findAny()
                .orElseThrow(NotAllowedMethodException::new);
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }
}
