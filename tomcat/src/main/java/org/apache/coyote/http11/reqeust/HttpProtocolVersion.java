package org.apache.coyote.http11.reqeust;

public enum HttpProtocolVersion {
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2_0("HTTP/2.0"),
    ;

    private final String version;

    HttpProtocolVersion(final String version) {
        this.version = version;
    }
}
