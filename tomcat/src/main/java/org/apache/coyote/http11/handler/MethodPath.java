package org.apache.coyote.http11.handler;

import java.util.Objects;
import org.apache.coyote.http11.request.HttpMethod;

public class MethodPath {
    private final HttpMethod httpMethod;
    private final String path;

    public MethodPath(HttpMethod httpMethod, String path) {
        this.httpMethod = httpMethod;
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MethodPath that = (MethodPath) o;
        return httpMethod == that.httpMethod && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, path);
    }
}
