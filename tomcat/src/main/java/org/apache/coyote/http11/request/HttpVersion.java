package org.apache.coyote.http11.request;

public class HttpVersion {
    private final String httpVersion;

    private HttpVersion(final String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public static HttpVersion from(final String httpVersion) {
        return new HttpVersion(httpVersion);
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
