package org.apache.coyote.http11;

import java.util.Arrays;

public enum Extension {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "text/javascript");

    private final String extension;
    private final String contentType;

    Extension(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static Extension from(String extension) {
        return Arrays.stream(values())
                .filter(it -> extension.contains(it.extension))
                .findAny()
                .orElse(HTML);
    }

    public String getContentType() {
        return contentType;
    }
}
