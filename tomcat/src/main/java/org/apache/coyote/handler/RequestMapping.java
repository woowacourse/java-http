package org.apache.coyote.handler;

import java.util.Objects;

public class RequestMapping {

    private final String httpMethod;
    private final String path;

    public RequestMapping(String httpMethod, String path) {
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
        RequestMapping that = (RequestMapping) o;
        return Objects.equals(httpMethod, that.httpMethod) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, path);
    }
}
