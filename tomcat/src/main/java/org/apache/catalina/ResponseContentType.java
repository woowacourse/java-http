package org.apache.catalina;

import java.util.Arrays;

public enum ResponseContentType {

    TEXT_HTML("text/html;charset=utf-8", ".html"),
    TEXT_CSS("text/css", ".css"),
    TEXT_JAVASCRIPT("text/javascript", ".js"),
    ICO("image/x-icon", ".ico");

    private final String type;
    private final String extension;

    ResponseContentType(final String type, final String extension) {
        this.type = type;
        this.extension = extension;
    }

    public static ResponseContentType from(final String resource) {
        return Arrays.stream(values())
                .filter(contentType -> resource.endsWith(contentType.extension))
                .findAny()
                .orElse(TEXT_HTML);
    }

    public String getType() {
        return type;
    }
}
