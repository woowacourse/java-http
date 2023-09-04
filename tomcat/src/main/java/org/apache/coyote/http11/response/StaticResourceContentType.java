package org.apache.coyote.http11.response;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;

public enum StaticResourceContentType {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JAVASCRIPT(".js", "text/javascript");

    private final String extension;
    private final String type;

    StaticResourceContentType(final String extension, final String value) {
        this.extension = extension;
        this.type = value;
    }

    public static Optional<StaticResourceContentType> find(final String targetUrl) {
        return Arrays.stream(values())
            .filter(contentType -> targetUrl.endsWith(contentType.extension))
            .findFirst();
    }

    public String convertToHeaderMessage(final Charset charset) {
        return type + ";charset=" + charset.toString().toLowerCase();
    }
}
