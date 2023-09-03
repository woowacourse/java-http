package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html", ".html"),
    CSS("text/css", ".css"),
    JS("application/javascript", ".js"),
    ICO("image/x-icon", ".ico");

    final String contentType;
    final String fileExtension;

    ContentType(final String contentType, final String fileExtension) {
        this.contentType = contentType;
        this.fileExtension = fileExtension;
    }

    public static boolean checkFileExtension(final String requestURI) {
        return Arrays.stream(values())
                .anyMatch(it -> requestURI.contains(it.fileExtension));
    }

    public static ContentType findContentTypeByURI(final String requestURI) {
        return Arrays.stream(values())
                .filter(it -> requestURI.contains(it.fileExtension))
                .findFirst()
                .orElse(HTML);
    }

    public String getContentType() {
        return contentType;
    }
}
