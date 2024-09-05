package org.apache.coyote.http11;

public class StaticResourceHandler implements ResourceHandler {

    private static final String PREFIX = "static";
    private static final String EMPTY_URI = "/";
    private static final String DEFAULT_URI = "/index.html";

    private final String uri;

    public StaticResourceHandler(String uri) {
        this.uri = readUri(uri);
    }

    private String readUri(String uri) {
        if (EMPTY_URI.equals(uri)) {
            return DEFAULT_URI;
        }
        return uri;
    }

    @Override
    public String handle() {
        return PREFIX.concat(uri);
    }
}
