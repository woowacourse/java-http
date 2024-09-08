package org.apache.catalina.controller;

import java.util.List;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpEndpoint {

    private final String path;
    private final HttpMethod method;
    private final List<String> queryParameters;

    public HttpEndpoint(String path, HttpMethod method, List<String> queryParameters) {
        this.path = path;
        this.method = method;
        this.queryParameters = queryParameters;
    }

    public static HttpEndpoint of(HttpRequest httpRequest) {
        return new HttpEndpoint(
                httpRequest.getPath(),
                httpRequest.getMethod(),
                null
        );
    }
}

