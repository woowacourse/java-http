package com.techcourse.http.common;

public enum HttpVersion {

    HTTP_1_0("1.0"),
    HTTP_1_1("1.1"),
    HTTP_2("2"),
    HTTP_3("3"),
    ;

    private static final String HTTP_VERSION_PREFIX = "HTTP/";

    private final String version;

    HttpVersion(final String version) {
        this.version = version;
    }

    public String toProtocolString() {
        return HTTP_VERSION_PREFIX + version;
    }
}
