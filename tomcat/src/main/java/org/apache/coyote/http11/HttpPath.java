package org.apache.coyote.http11;

import java.util.Objects;

public class HttpPath {
    private final String path;

    public HttpPath(String path) {
        this.path = Objects.requireNonNull(path);
    }

    public boolean startsWith(String path) {
        return this.path.startsWith(path);
    }
}
