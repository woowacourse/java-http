package org.apache.coyote.http11.handler;

import java.util.Objects;

public class ControllerInfo {
    private final HttpMethod httpMethod;
    private final String path;

    public ControllerInfo(HttpMethod httpMethod, String path) {
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
        ControllerInfo that = (ControllerInfo) o;
        return httpMethod == that.httpMethod && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, path);
    }
}
