package org.apache.coyote.http11;

public enum ContentType {

    HTML("text/html;charset=utf-8"),
    CSS("text/css;charset=utf-8"),
    JS("application/javascript;charset=utf-8");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public static ContentType from(String path) {
        if (path.endsWith(".css")) {
            return CSS;
        }
        if (path.endsWith(".js")) {
            return JS;
        }
        return HTML;
    }

    public String getValue() {
        return value;
    }
}
