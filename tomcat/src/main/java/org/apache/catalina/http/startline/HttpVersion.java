package org.apache.catalina.http.startline;

import org.apache.catalina.exception.CatalinaException;

public enum HttpVersion {

    HTTP11("HTTP/1.1"),
    ;

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion parse(String value) {
        if (HTTP11.getValue().equals(value)) {
            return HTTP11;
        }
        throw new CatalinaException("Invalid HTTP version: " + value);
    }

    public String getValue() {
        return value;
    }
}
