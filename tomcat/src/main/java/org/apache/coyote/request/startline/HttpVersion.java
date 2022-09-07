package org.apache.coyote.request.startline;

public class HttpVersion {

    private final String version;

    private HttpVersion(final String version) {
        this.version = version;
    }

    public static HttpVersion from(String version) {
        return new HttpVersion(version);
    }

    public String getVersion() {
        return version;
    }
}
