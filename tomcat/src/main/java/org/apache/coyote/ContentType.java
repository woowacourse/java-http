package org.apache.coyote;

import java.util.Arrays;

public enum ContentType {
    HTML(".html", "text/html"),
    CSS(".css", "text/css"),
    JS(".js", "application/x-javascript"),
    ICO(".ico", "image/x-icon");

    private final String extension;
    private final String type;

    ContentType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static ContentType find(String filePath) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> filePath.contains(contentType.getExtension()))
                .findFirst().orElse(ContentType.HTML);
    }

    public String getExtension() {
        return extension;
    }

    public String getType() {
        return type;
    }
}
