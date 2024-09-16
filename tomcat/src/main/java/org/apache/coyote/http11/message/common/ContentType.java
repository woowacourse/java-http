package org.apache.coyote.http11.message.common;

public enum ContentType {
    HTTP("text/html;charset=utf-8"),
    CSS("text/css;charset=utf-8"),
    JS("text/javascript;charset=utf-8"),
    SVG("image/svg+xml");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public static ContentType from(String uri) {
        if (uri.endsWith("css")) {
            return CSS;
        }
        if (uri.endsWith("js")) {
            return JS;
        }
        if (uri.endsWith("svg")) {
            return JS;
        }
        return HTTP;
    }

    public String getValue() {
        return value;
    }
}
