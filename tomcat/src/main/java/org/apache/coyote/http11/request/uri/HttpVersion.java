package org.apache.coyote.http11.request.uri;

public class HttpVersion {

    private final String version;

    private HttpVersion(final String version) {
        this.version = version;
    }

    public static HttpVersion from(final String version) {
        return new HttpVersion(version);
    }

    public String getVersion() {
        return version;
    }
}
