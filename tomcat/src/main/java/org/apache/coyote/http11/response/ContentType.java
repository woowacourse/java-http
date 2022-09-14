package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html"),
    CSS("text/css"),
    JS("text/css"),
    APPLICATION_FORM_UNRECORDED("application/x-www-form-urlencoded");


    private final String value;

    ContentType(final String value) {
        this.value = value;
    }

    public static ContentType from(final String path) {
        if (path.equals("/")) {
            return HTML;
        }
        return Arrays.stream(ContentType.values())
                .filter(it -> path.endsWith(it.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Content-type: " + path + "을 찾을 수 없습니다."));
    }

    private String getName() {
        return this.name().toLowerCase();
    }

    public String getValue() {
        return value;
    }
}
