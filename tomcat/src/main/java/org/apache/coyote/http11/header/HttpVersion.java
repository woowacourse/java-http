package org.apache.coyote.http11.header;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("HTTP/2");

    private final String version;

    HttpVersion(final String version) {
        this.version = version;
    }

    public static HttpVersion of(final String version) {
        if (version.equals("HTTP/1.1")) {
            return HTTP_1_1;
        }
        return HTTP_2;
    }

    public String getVersion() {
        return version;
    }
}
