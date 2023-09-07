package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.http11.request.RequestLine;

public enum HttpExtensionType {

    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css"),
    JS(".js", "text/javascript"),
    ICO(".ico", "image/svg+xml"),
    SVG(".svg", "image/svg+xml");

    private final String extension;
    private final String contentType;

    HttpExtensionType(final String extension, final String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static HttpExtensionType from(final String extension) {
        return Arrays.stream(values())
                .filter(it -> extension.contains(it.extension))
                .findAny()
                .orElse(HTML);
    }

    public static HttpExtensionType from(final RequestLine requestLine) {
        return Arrays.stream(values())
                .filter(it -> requestLine.hasFileExtension(it.getExtension()))
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
