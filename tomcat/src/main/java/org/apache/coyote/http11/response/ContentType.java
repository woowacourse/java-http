package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {

    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "text/javascript"),
    IMAGE_X_ICON("ico", "image/x-icon");

    private final String extension;
    private final String contentType;

    ContentType(final String extension, final String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static ContentType from(final String requestFirstLine) {
        final int dotIndex = requestFirstLine.lastIndexOf(".");
        final String extension = requestFirstLine.substring(dotIndex);

        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.isSameExtension(extension))
                .findFirst()
                .orElse(HTML);
    }

    private boolean isSameExtension(String extension) {
        return this.extension.equals(extension);
    }

    public String getContentType() {
        return contentType;
    }
}
