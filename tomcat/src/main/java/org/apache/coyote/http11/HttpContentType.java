package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpContentType {
    HTML("html", "text/html;charset=utf-8 "),
    CSS("css", "text/css "),
    JS("js", "text/js"),
    ICO("ico", "image/x-icon");

    private final String extension;
    private final String contentType;

    HttpContentType(final String extension, final String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static HttpContentType valueOfCotentType(String requestExtension) {

        return Arrays.stream(values())
                .filter(value -> value.extension.equals(requestExtension))
                .findAny()
                .orElse(HTML);
    }

    public String getContentType() {
        return contentType;
    }

    public String getExtension() {
        return extension;
    }
}
