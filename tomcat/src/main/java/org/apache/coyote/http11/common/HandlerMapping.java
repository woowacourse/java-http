package org.apache.coyote.http11.common;

import java.util.Objects;

public class HandlerMapping {

    private final Method method;
    private final String path;

    public HandlerMapping(Method method, String path) {
        this.method = method;
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
        HandlerMapping that = (HandlerMapping) o;
        return method == that.method && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }
}
