package org.apache.coyote.common;

import java.util.Arrays;
import java.util.List;

public enum MediaTypes {

    HTML(List.of(".html", ".htm"), "text/html;charset=utf-8"),
    CSS(List.of(".css"), "text/css"),
    JAVASCRIPT(List.of(".js"), "application/javascript"),
    GIF(List.of(".gif"), "image/gif"),
    JPG(List.of(".jpg", ".jpeg"), "image/jpeg"),
    PNG(List.of(".png"), "image/png"),
    SVG(List.of(".svg"), "image/svg+xml"),
    DEFAULT(List.of(""), "text/plain");

    private final List<String> fileExtensions;
    private final String type;

    MediaTypes(final List<String> fileExtensions, final String type) {
        this.fileExtensions = fileExtensions;
        this.type = type;
    }

    public static String findMediaType(final String uri) {
        return Arrays.stream(MediaTypes.values())
                .filter(code -> isMatchingExtension(code.fileExtensions, uri))
                .findAny()
                .orElse(DEFAULT)
                .type;
    }

    private static boolean isMatchingExtension(final List<String> extensions, final String uri) {
        for (String extension : extensions) {
            if (uri.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
