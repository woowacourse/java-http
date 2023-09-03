package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html"),
    CSS("text/css"),
    JAVASCRIPT("text/javascript"),
    ;

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public static ContentType from(String other) {
        return Arrays.stream(values())
                .filter(content -> other.contains(content.getValue()))
                .findFirst()
                .orElse(HTML);
    }

    public String getValue() {
        return value;
    }

}
