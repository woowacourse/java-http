package org.apache.coyote.routing;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpMethod;

public record RouteKey(
        HttpMethod method,
        String path
) {
    public static RouteKey from(HttpRequest request) {
        return new RouteKey(HttpMethod.parse(request.getMethod()), request.getPath());
    }
}
