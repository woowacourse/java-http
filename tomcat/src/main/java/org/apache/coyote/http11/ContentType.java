package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/javascript"),
    SVG("svg", "image/svg+xml");

    private final String extension;
    private final String value;

    ContentType(String extension, String value) {
        this.extension = extension;
        this.value = value;
    }

    // 요청 path의 확장자로 Content-Type을 찾고, 없으면 HTML로 응답
    public static ContentType from(final String path) {
        return Arrays.stream(values())
                .filter(type -> path.endsWith("." + type.extension))
                .findFirst()
                .orElse(HTML);
    }

    public String getValue() {
        return value;
    }
}
