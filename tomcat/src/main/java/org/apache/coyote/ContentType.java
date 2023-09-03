package org.apache.coyote;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JAVASCRIPT("text/javascript", "js"),
    ;

    private final String value;
    private final String extension;

    ContentType(String value, String extension) {
        this.value = value;
        this.extension = extension;
    }

    public static ContentType from(String other) {
        return Arrays.stream(values())
                .filter(content -> other.contains(content.getExtension()))
                .findFirst()
                .orElse(HTML);
    }

    public String getValue() {
        return value;
    }

    public String getExtension() {
        return extension;
    }
}
