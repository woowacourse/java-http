package org.apache.coyote.http11.http;


public enum ContentType {

    JSON("application/json;charset=utf-8"),
    HTML("text/html;charset=utf-8"),
    PLAIN("text/plain;charset=utf-8"),
    XML("application/xml;charset=utf-8"),
    JAVASCRIPT("application/javascript;charset=utf-8"),
    FORM_URLENCODED("application/x-www-form-urlencoded;charset=utf-8"),
    MULTIPART("multipart/form-data"),
    DEFAULT("application/octet-stream");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
