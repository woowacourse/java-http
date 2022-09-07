package org.apache.coyote.http11.response;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum ContentType {

    CSS("text/css", "css"),
    HTML("text/html", "html"),
    JAVASCRIPT("text/javascript", "js");

    private final String contentType;
    private final String extension;

    ContentType(String contentType, String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public static boolean isExistExtension(String path) {
        String extension = path.substring(path.lastIndexOf(".") + 1);

        return Arrays.stream(values())
                .anyMatch(contentType -> contentType.getExtension().equals(extension));
    }

    public static String findContentType(String path) {
        String extension = path.substring(path.lastIndexOf(".") + 1);

        return Arrays.stream(values())
                .filter(contentType -> contentType.getExtension().equals(extension))
                .map(ContentType::getContentType)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public String getContentType() {
        return contentType;
    }

    public String getExtension() {
        return extension;
    }
}
