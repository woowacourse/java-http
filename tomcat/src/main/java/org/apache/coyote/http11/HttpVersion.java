package org.apache.coyote.http11;

public enum HttpVersion {
    ONE_ONE("HTTP/1.1");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
