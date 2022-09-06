package org.apache.constant;

import java.util.HashMap;
import java.util.Map;

public enum MediaType {

    PLAIN("text/plain", "plain"),
    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JAVA_SCRIPT("text/javascript", "js"),
    JSON("application/json", "json");

    private static final Map<String, String> CACHE = new HashMap<>(); // O(1)

    private final String value;
    private final String fileExtension;

    MediaType(final String value, final String fileExtension) {
        this.value = value;
        this.fileExtension = fileExtension;
    }

    static {
        for (final MediaType mediaType : values()) {
            CACHE.putIfAbsent(mediaType.fileExtension, mediaType.value);
        }
    }

    public static String find(final String fileExtension) {
        final String value = CACHE.get(fileExtension);
        if (value == null) {
            throw new IllegalArgumentException("존재하지 않는 파일 확장자 입니다: " + fileExtension);
        }
        return value;
    }

    public String value() {
        return value;
    }

    public String fileExtension() {
        return fileExtension;
    }
}
