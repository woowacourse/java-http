package org.apache.coyote.http11.model;

import java.util.Arrays;

public enum ContentType {

    CSS("css", "text/css"),
    JS("js", "application/javascript"),
    HTML("html", "text/html"),
    ICO("ico", "image/x-icon"),
    ;

    private final String fileExtension;
    private final String value;

    ContentType(final String fileExtension, final String value) {
        this.fileExtension = fileExtension;
        this.value = value;
    }

    public static ContentType of(final String fileFormat) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> fileFormat.equals(contentType.fileExtension))
                .findAny()
                .orElse(HTML);
    }

    public String getValue() {
        return value;
    }
}
