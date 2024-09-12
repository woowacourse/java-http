package org.apache.catalina.handler;

import java.util.Objects;
import org.apache.coyote.http11.request.HttpMethod;

public class RequestMapping {

    private final HttpMethod httpMethod;
    private final String path;

    public RequestMapping(HttpMethod httpMethod, String path) {
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
