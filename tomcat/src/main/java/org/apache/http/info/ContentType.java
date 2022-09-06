package org.apache.http.info;

import java.util.Arrays;
import java.util.Objects;

public enum ContentType {

    TEXT_HTML("html", "text/html;charset=utf-8"),
    TEXT_CSS("css", "text/css; charset=utf-8"),
    TEXT_JAVASCRIPT("js", "text/javascript; charset=utf-8"),
    IMAGE_GIF("gif", "image/gif"),
    IMAGE_PNG("png", "image/png"),
    IMAGE_SVG("svg", "image/svg"),
    FAVICON("ico", "image/x-icon"),
    ;

    private final String suffix;
    private final String name;

    ContentType(final String suffix, final String name) {
        this.suffix = suffix;
        this.name = name;
    }

    public static ContentType from(String suffix) {
        return Arrays.stream(values())
                .filter(value -> Objects.equals(value.suffix, suffix))
                .findAny()
                .orElseGet(() -> TEXT_HTML);
    }

    public String getName() {
        return this.name;
    }
}
