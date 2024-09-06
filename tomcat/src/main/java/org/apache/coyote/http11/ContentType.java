package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("text/js"),
    ;

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return value.substring(value.indexOf("/") + 1);
    }

    public static ContentType findByUrl(String url) {
        return Arrays.stream(values())
                .filter(contentType -> url.endsWith(contentType.getType()))
                .findAny()
                .orElse(HTML);
    }
}
