package org.apache.coyote.http11.dispatcher;

import java.util.Objects;

public class RouteKey {

    private final String method;
    private final String requestMapping;

    public RouteKey(String method, String requestMapping) {
        this.method = method;
        this.requestMapping = requestMapping;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RouteKey routeKey = (RouteKey) o;
        return Objects.equals(method, routeKey.method) && Objects.equals(requestMapping,
                routeKey.requestMapping);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, requestMapping);
    }
}
