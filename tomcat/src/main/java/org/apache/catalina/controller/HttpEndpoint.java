package org.apache.catalina.controller;

import java.util.Objects;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;


public class HttpEndpoint {

    private final String path;
    private final HttpMethod method;

    public HttpEndpoint(String path, HttpMethod method) {
        this.path = path;
        this.method = method;
    }

    public static HttpEndpoint of(HttpRequest httpRequest) {
        return new HttpEndpoint(
                httpRequest.getPath(),
                httpRequest.getMethod()
        );
    }

    public boolean matches(HttpRequest request) {
        HttpEndpoint requestEndpoint = HttpEndpoint.of(request);
        return equals(requestEndpoint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpEndpoint endpoint = (HttpEndpoint) o;
        return Objects.equals(path, endpoint.path) && method == endpoint.method;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, method);
    }
}

