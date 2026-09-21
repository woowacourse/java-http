package org.apache.coyote.http11;

public enum ContentType {
    TEXT_HTML("text/html"),
    TEXT_JAVASCRIPT("text/javascript"),
    TEXT_CSS("text/css");

    private String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getWithUTF8Encoding() {
        return value + ";charset=utf-8";
    }
}
