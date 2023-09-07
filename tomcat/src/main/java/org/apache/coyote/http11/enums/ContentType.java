package org.apache.coyote.http11.enums;

import java.util.Arrays;

public enum ContentType {

    JS(".js","Application/javascript;"),
    CSS(".css","text/css;"),
    HTML(".html","text/html;");

    private final String fileType;
    private final String path;

    ContentType(final String fileType, final String path) {
        this.fileType = fileType;
        this.path = path;
    }

    public static String getContentType(final String path) {
        return Arrays.stream(ContentType.values())
                .filter(value -> path.endsWith(value.getFileType()))
                .findFirst()
                .orElse(HTML)
                .getPath();
    }

    public String getFileType() {
        return fileType;
    }

    public String getPath() {
        return path;
    }
}
