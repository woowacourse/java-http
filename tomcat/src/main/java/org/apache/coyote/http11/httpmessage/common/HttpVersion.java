package org.apache.coyote.http11.httpmessage.common;

public enum HttpVersion {
    HTTP1_0("HTTP/1.0"),
    HTTP1_1("HTTP/1.1"),
    HTTP2_0("HTTP/2.0"),
    HTTP3_0("HTTP/3.0");

    private final String version;

    HttpVersion(final String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
