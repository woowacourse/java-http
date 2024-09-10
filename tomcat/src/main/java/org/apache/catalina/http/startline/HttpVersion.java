package org.apache.catalina.http.startline;

import java.util.Arrays;
import org.apache.catalina.exception.CatalinaException;

public enum HttpVersion {

    HTTP11("HTTP/1.1"),
    ;

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion parse(String value) {
        return Arrays.stream(HttpVersion.values())
                .filter(v -> v.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new CatalinaException("Invalid HTTP version: " + value));
    }

    public String getValue() {
        return value;
    }
}
