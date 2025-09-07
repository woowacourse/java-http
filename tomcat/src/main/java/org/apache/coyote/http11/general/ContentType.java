package org.apache.coyote.http11.general;

public enum ContentType {

    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    APPLICATION_JSON("application/json"),
    ;

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValueWithUtf8Charset() {
        return this.value + ";charset=utf-8";
    }
}
