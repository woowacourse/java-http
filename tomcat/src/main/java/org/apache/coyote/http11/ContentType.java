package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/js"),
    TEXT("txt", "text"),
    JSON("json", "text/json"),
    ;

    final String expansion;
    final String responseContentType;

    ContentType(String expansion, String responseContentType) {
        this.expansion = expansion;
        this.responseContentType = responseContentType;
    }

    public static ContentType from(String expansion) {
        return Arrays.stream(values())
                .filter(type -> type.expansion.equals(expansion))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 타입입니다."));
    }
}
