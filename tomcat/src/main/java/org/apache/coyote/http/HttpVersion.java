package org.apache.coyote.http;

public enum HttpVersion {
    HTTP11("HTTP/1.1"),
    ;

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
