package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html", ".html"),
    CSS("text/css", ".css"),
    JS("application/javascript", ".js"),
    ICO("image/x-icon", ".ico");

    final String type;
    final String fileExtension;

    ContentType(final String type, final String fileExtension) {
        this.type = type;
        this.fileExtension = fileExtension;
    }

    public static String getByPath(final String path) {
        return Arrays.stream(values())
                .filter(it -> path.endsWith(it.getFileExtension()))
                .map(ContentType::getType)
                .findAny()
                .orElse(HTML.getType());
    }

    public String getType() {
        return type;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
