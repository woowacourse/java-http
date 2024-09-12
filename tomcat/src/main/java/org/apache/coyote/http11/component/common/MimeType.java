package org.apache.coyote.http11.component.common;

import java.util.stream.Stream;

public enum MimeType {
    TEXT_PLAIN("", "text/plain"),
    TEXT_CSS(".css", "text/css"),
    TEXT_JS(".js", "text/javascript"),
    TEXT_HTML(".html", "text/html"),
    IMAGES_SVG(".svg", "image/svg+xml");

    private final String suffix;
    private final String mimeText;

    MimeType(final String suffix, final String mimeText) {
        this.suffix = suffix;
        this.mimeText = mimeText;
    }

    public static String find(final String uriPath) {
        return Stream.of(values())
                .filter(mime -> uriPath.endsWith(mime.suffix) && mime != TEXT_PLAIN)
                .map(mime -> mime.mimeText)
                .findAny()
                .orElseGet(() -> TEXT_PLAIN.mimeText);
    }

    public String getSuffix() {
        return suffix;
    }

    public String getMimeText() {
        return mimeText;
    }
}
