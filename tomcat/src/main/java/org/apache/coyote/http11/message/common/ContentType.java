package org.apache.coyote.http11.message.common;

import java.util.Arrays;

public enum ContentType {

    TEXT_HTML("text/html;charset=utf-8 "),
    TEXT_JS("text/javascript;charset=utf-8"),
    TEXT_CSS("text/css;charset=utf-8"),
    FORM_DATA("application/x-www-form-urlencoded");

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public static ContentType from(String type) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.type.equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 ContentType."));
    }

    public String getType() {
        return type;
    }
}
