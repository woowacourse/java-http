package org.apache.coyote.http11.request;

import java.util.StringJoiner;

public class HttpPath {

    private final String path;

    public HttpPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpPath.class.getSimpleName() + "[", "]")
                .add("path='" + path + "'")
                .toString();
    }
}
