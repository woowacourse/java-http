package org.apache.coyote.common;

public enum HttpContentType {
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html;charset=utf-8"),
    TEXT_CSS("text/css"),
    APPLICATION_JAVASCRIPT("application/javascript"),
    IMAGE_SVG("image/svg+xml"),
    ;

    private final String value;

    HttpContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
