package org.apache.http.info;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1"),
    ;

    private final String name;

    HttpVersion(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
