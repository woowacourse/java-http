package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    CSS(".css", "text/css"),
    JS(".js", "application/javascript"),
    HTML(".html", "text/html;charset=utf-8"),
    ICO(".ico", "image/x-icon");

    private final String extension;
    private final String mimeType;

    ContentType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static String fromPath(String path) {
        return Arrays.stream(values())
                .filter(type -> path.endsWith(type.extension))
                .findFirst()
                .map(ContentType::getMimeType)
                .orElse(HTML.getMimeType());
    }
}
