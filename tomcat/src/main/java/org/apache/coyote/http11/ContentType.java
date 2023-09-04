package org.apache.coyote.http11;

public enum ContentType {

    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    APPLICATION_JS("application/js"),
    WILD_CARD("*/*");

    private static final String CSS = "css";
    private static final String HTML = "html";
    private static final String JS = "js";

    private String value;

    ContentType(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ContentType from(String extension) {
        if (extension.equalsIgnoreCase(CSS)) {
            return TEXT_CSS;
        }
        if (extension.equalsIgnoreCase(HTML)) {
            return TEXT_HTML;
        }
        if (extension.equalsIgnoreCase(JS)) {
            return APPLICATION_JS;
        }
        return WILD_CARD;
    }
}
