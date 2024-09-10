package org.apache.coyote.http11.config;

import java.util.HashSet;
import java.util.Set;
import org.apache.coyote.http11.httprequest.HttpRequest;

public class UnauthorizedInterceptor {

    private final Set<String> unauthorizedPaths = new HashSet<>();

    public UnauthorizedInterceptor() {
        unauthorizedPaths.add("/401");
        unauthorizedPaths.add("/404");
        unauthorizedPaths.add("/500");
        unauthorizedPaths.add("/401.html");
        unauthorizedPaths.add("/404.html");
        unauthorizedPaths.add("/500.html");
    }

    public boolean checkPath(HttpRequest httpRequest) {
        return !httpRequest.containsKey("Location") && unauthorizedPaths.contains(httpRequest.getPath());
    }
}
