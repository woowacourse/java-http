package org.apache.coyote.http11.model;

import java.util.Arrays;

public enum ContentType {

    CSS("css", "text/css"),
    JS("js", "application/javascript"),
    HTML("html", "text/html"),
    ;

    private static final String ERROR_MESSAGE = "존재하지 않는 컨텐츠 타입입니다. -> contentType: %s";

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
