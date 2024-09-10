package org.apache.coyote.http;

public enum MediaType {

    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    APPLICATION_JAVASCRIPT("application/javascript"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    ;

    private final String value;

    public static final String DEFAULT_CHARSET = "charset=utf-8";

    MediaType(String value) {
        this.value = value;
    }

    public String defaultCharset() {
        return value + ";" + DEFAULT_CHARSET;
    }

    public String value() {
        return value;
    }
}
