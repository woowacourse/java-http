package org.apache.coyote.http11.message;

import java.util.Arrays;
import org.apache.coyote.http11.message.exception.UnsupportedFileException;

public enum ContentType {

    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "text/javascript"),
    ICO(".ico", "image/x-icon");

    private final String extension;
    private final String headerValue;

    ContentType(final String extension, final String headerValue) {
        this.extension = extension;
        this.headerValue = headerValue;
    }

    public static ContentType findByFileName(final String fileName) {
        final StaticFile file = new StaticFile(fileName);
        return findByExtension(file.getExtension());
    }

    public static ContentType findByExtension(final String extension) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.extension.equals(extension))
                .findFirst()
                .orElseThrow(() -> new UnsupportedFileException());
    }

    public String getExtension() {
        return extension;
    }

    public String getHeaderValue() {
        return headerValue;
    }
}
