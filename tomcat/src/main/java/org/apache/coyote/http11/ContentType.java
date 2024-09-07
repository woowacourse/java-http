package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("text/js"),
    ;

    private String value;
    private String type;

    ContentType(String value) {
        this.value = value;
        this.type = value.substring(value.indexOf("/") + 1);
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public static ContentType findByUrl(String url) {
        return Arrays.stream(values())
                .filter(contentType -> url.endsWith(contentType.getType()))
                .findAny()
                .orElse(HTML);
    }
}
