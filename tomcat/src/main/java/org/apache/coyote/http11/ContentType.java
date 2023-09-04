package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("application/javascript");

    public final String value;

    ContentType(String value) {
        this.value = value;
    }

    public static ContentType from(String fileName) {
        String value = fileName.substring(fileName.lastIndexOf(".") + 1);
        return Arrays.stream(values())
            .filter(it -> it.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
