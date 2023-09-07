package org.apache.coyote.http11.response;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;

public enum ContentType {
    CSS(".css", "text/css"),
    HTML(".html", "text/html"),
    HTML_UTF8(".html", "text/html;charset=utf-8"),
    JAVASCRIPT(".js", "text/javascript");

    private final String extension;
    private final String type;

    ContentType(final String extension, final String value) {
        this.extension = extension;
        this.type = value;
    }

    public static Optional<ContentType> find(final String targetUrl) {
        return Arrays.stream(values())
            .filter(contentType -> targetUrl.endsWith(contentType.extension))
            .findFirst();
    }

    public String getType() {
        return type;
    }

    public String withCharset(final Charset charset) {
        return type + ";charset=" + charset.toString().toLowerCase();
    }
}
