package org.apache.coyote.http11.request;

public class RequestVersion {
    private final String version;

    public RequestVersion(final String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
