package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    CSS(".css", "text/css"),
    ICO(".ico", "text/css"),
    JS(".js", "text/javascript"),
    HTML(".html", "text/html");

    private final String extension;
    private final String type;

    ContentType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static String findType(String path) {
        return Arrays.stream(values())
                .filter(contentType -> path.endsWith(contentType.extension))
                .map(contentType -> contentType.type)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 Content Type 입니다."));
    }

    public String getType() {
        return type;
    }
}
