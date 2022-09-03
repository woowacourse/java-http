package org.apache.coyote.http11;

import java.util.Arrays;

public enum Extension {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/js"),
    CSV("svg", "image/svg+xml"),
    ICO("ico", "image/x-icon"),
    ;
    private final String name;
    private final String contentType;

    Extension(String name, String contentType) {
        this.name = name;
        this.contentType = contentType;
    }

    public static String convertToContentType(String url) {
        int index = url.indexOf(".");
        if (index == -1) {
            return HTML.contentType;
        }
        Extension extension1 = Arrays.stream(values())
                .filter(extension -> url.endsWith(extension.name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알맞은 확장자가 없습니다."));
        return extension1.contentType;
    }
}
