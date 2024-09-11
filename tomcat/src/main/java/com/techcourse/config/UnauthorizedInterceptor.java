package com.techcourse.config;

import java.util.Set;
import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.httprequest.HttpRequest;

public class UnauthorizedInterceptor {

    private final Set<String> unauthorizedPaths;

    public UnauthorizedInterceptor() {
        unauthorizedPaths = Set.of("/401", "/404", "/500", "/401.html", "/404.html", "/500.html");
    }

    public boolean isInterceptPath(HttpRequest httpRequest) {
        return !httpRequest.containsHeader(HttpHeaderName.LOCATION) && unauthorizedPaths.contains(httpRequest.getPath());
    }
}
