package org.apache.coyote.request;

public class RequestPath {

    private static final String DEFAULT_REQUEST_PATH = "/";

    private final String source;

    public RequestPath(final String source) {
        this.source = source;
    }

    public boolean isDefault() {
        return source.equals(DEFAULT_REQUEST_PATH);
    }

    public String source() {
        return source;
    }

    @Override
    public String toString() {
        return source;
    }
}
