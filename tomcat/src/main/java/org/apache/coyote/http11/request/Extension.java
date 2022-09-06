package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum Extension {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "text/javascript"),
    ICO(".ico", "image/x-icon"),
    NONE("null", "text/html")
    ;

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
                .orElse(NONE);
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isIco() {
        return this == Extension.ICO;
    }
}
