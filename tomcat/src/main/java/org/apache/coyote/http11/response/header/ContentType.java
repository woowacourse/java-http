package org.apache.coyote.http11.response.header;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html", ".html"),
    CSS("text/css", ".css"),
    JS("text/javascript", ".js"),
    ICO("image/x-icon", ".ico"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded", "");

    private static final String CHARSET = ";charset=utf-8";

    private final String mediaType;
    private final String extension;

    ContentType(String mediaType, String extension) {
        this.mediaType = mediaType;
        this.extension = extension;
    }

    public static boolean isStaticResource(String path) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.getExtension() != null && !contentType.getExtension().isEmpty())
                .anyMatch(contentType -> path.toLowerCase().endsWith(contentType.getExtension()));
    }

    public static ContentType findByExtension(String path) {
        return Arrays.stream(values())
                .filter(contentType -> path.toLowerCase().endsWith(contentType.getExtension()))
                .findFirst()
                .orElse(HTML);
    }

    public static ContentType findByMediaType(String mediaType) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.mediaType.equalsIgnoreCase(mediaType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("MediaType과 일치하는 ContentType이 존재하지 않습니다."));
    }

    public String value() {
        return mediaType + CHARSET;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getExtension() {
        return extension;
    }
}
