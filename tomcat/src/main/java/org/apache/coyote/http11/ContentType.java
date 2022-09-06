package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html;charset=utf-8", ".html"),
    CSS("text/css", ".css"),
    JAVASCRIPT("application/js", ".js"),
    SVG("image/svg+xml", ".svg")
    ;

    private final String value;
    private final String fileNameExtension;

    ContentType(String value, String fileNameExtension) {
        this.value = value;
        this.fileNameExtension = fileNameExtension;
    }

    public static ContentType from(String uri) {
        return Arrays.stream(values())
                .filter(contentType -> uri.endsWith(contentType.fileNameExtension))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(uri + "에 해당하는 contentType은 없습니다."));
    }

    public String value() {
        return this.value;
    }
}
