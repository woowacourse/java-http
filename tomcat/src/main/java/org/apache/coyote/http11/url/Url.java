package org.apache.coyote.http11.url;

import org.apache.coyote.http11.dto.Http11Request;

public abstract class Url {
    private final String path;

    protected Url(String path) {
        this.path = path;
    }

    public abstract Http11Request getRequest();

    public String getPath() {
        return path;
    }
}
