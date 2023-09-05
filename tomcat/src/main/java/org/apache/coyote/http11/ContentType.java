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

    public static String getByPath(final String path) {
        return Arrays.stream(values())
                .filter(it -> path.endsWith(it.getFileExtension()))
                .map(ContentType::getContentType)
                .findAny()
                .orElse(HTML.getContentType());
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
