package org.apache.coyote.http;

import java.util.Arrays;

public enum ContentType {

    TEXT_HTML("text/html", "html"),
    TEXT_CSS("text/css", "css"),
    TEXT_JAVASCRIPT("text/javascript", "js"),
    APPLICATION_X_WWW_FORM_URL_ENCODED("application/x-www-form-urlencoded", ""),
    ;

    public final String value;
    public final String identifier;

    ContentType(String value, String identifier) {
        this.value = value;
        this.identifier = identifier;
    }

    public static ContentType from(String value) {
        return Arrays.stream(values())
                .filter(contentType -> value.endsWith(contentType.identifier))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 타입입니다. 입력: " + value));
    }
}
