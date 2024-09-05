package org.apache.coyote.http11;

public enum ContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("text/js"),
    ;

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    public static ContentType findByUrl(String url) {
        if (url.contains(".css")) {
            return CSS;
        }
        if (url.contains(".js")) {
            return JS;
        }
        return HTML;
    }

    public String getValue() {
        return value;
    }
}
