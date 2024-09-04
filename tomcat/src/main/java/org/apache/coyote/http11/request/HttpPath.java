package org.apache.coyote.http11.request;

import java.util.Objects;
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
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        HttpPath httpPath = (HttpPath) object;
        return Objects.equals(path, httpPath.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpPath.class.getSimpleName() + "[", "]")
                .add("path='" + path + "'")
                .toString();
    }
}
