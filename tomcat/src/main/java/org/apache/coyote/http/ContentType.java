package org.apache.coyote.http;

import java.util.Arrays;

public enum ContentType {
    CSS("css", "text/css"),
    JS("js", "application/javascript"),
    HTML("html", "text/html"),
    ICO("ico", "image/x-icon");

    private final String fileExtension;
    private final String value;


    ContentType(String fileExtension, String value) {
        this.fileExtension = fileExtension;
        this.value = value;
    }

    public static ContentType from(final String inputFileExtension) {
        return Arrays.stream(ContentType.values())
                .filter(it -> it.fileExtension.equals(inputFileExtension))
                .findFirst()
                .orElse(HTML);
    }

    public String getValue() {
        return value;
    }
}
