package org.apache.coyote.http11.url;

import org.apache.coyote.http11.response.Http11Response;

public abstract class Url {
    private final String path;

    protected Url(String path) {
        this.path = path;
    }

    public abstract Http11Response getResource();

    public String getPath() {
        return path;
    }
}
