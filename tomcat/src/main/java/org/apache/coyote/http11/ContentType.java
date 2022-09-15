package org.apache.coyote.http11;

import java.io.File;
import java.util.Arrays;

public enum ContentType {

    PLAIN("", "text/plain; charset=utf-8"),
    HTML("html", "text/html; charset=utf-8"),
    CSS("css", "text/css; charset=utf-8"),
    JS("js", "text/javascript; charset=utf-8"),
    ICO("ico", "image/x-icon"),
    SVG("svg", "image/svg+xml");

    private static final String FILE_EXTENSION_DELIMITER = ".";

    private final String fileExtension;
    private final String value;

    ContentType(final String fileExtension, final String value) {
        this.fileExtension = fileExtension;
        this.value = value;
    }

    public static ContentType of(final File file) {
        final String fileExtension = getFileExtension(file);
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.fileExtension.equals(fileExtension))
                .findFirst()
                .orElseGet(ContentType::getDefaultContentType);
    }

    private static String getFileExtension(final File file) {
        final String path = file.getPath();
        if (path.contains(FILE_EXTENSION_DELIMITER)) {
            final int index = path.lastIndexOf(FILE_EXTENSION_DELIMITER);
            return path.substring(index + 1);
        }
        return "html";
    }

    private static ContentType getDefaultContentType() {
        return PLAIN;
    }

    public String getValue() {
        return value;
    }
}

