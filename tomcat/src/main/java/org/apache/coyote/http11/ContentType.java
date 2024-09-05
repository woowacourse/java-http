package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html", ".html"),
    CSS("text/css", ".css"),
    JS("text/javascript", ".js"),
    ICO("image/x-icon", ".ico");

    private static final String CHARSET = "charset=utf-8";

    private final String mediaType;
    private final String extension;

    ContentType(String mediaType, String extension) {
        this.mediaType = mediaType;
        this.extension = extension;
    }

    public static String findWithCharset(String path) {
        return find(path).mediaType + ";" + CHARSET;
    }

    public static ContentType find(String path) {
        return Arrays.stream(values())
                .filter(contentType -> path.toLowerCase().endsWith(contentType.getExtension()))
                .findFirst()
                .orElse(HTML);
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getExtension() {
        return extension;
    }
}
