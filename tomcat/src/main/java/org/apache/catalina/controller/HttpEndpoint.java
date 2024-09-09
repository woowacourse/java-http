package org.apache.catalina.controller;

import java.util.Objects;
import java.util.Set;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;


public class HttpEndpoint {

    private final String path;
    private final HttpMethod method;
    private final Set<String> parameters;

    public HttpEndpoint(String path, HttpMethod method) {
        this(path, method, Set.of());
    }

    public HttpEndpoint(String path, HttpMethod method, Set<String> parameters) {
        this.path = path;
        this.method = method;
        this.parameters = parameters;
    }

    public static HttpEndpoint of(HttpRequest httpRequest) {
        return new HttpEndpoint(
                httpRequest.getPath(),
                httpRequest.getMethod(),
                httpRequest.getQueryKeys()
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
        return Objects.equals(path, endpoint.path) && method == endpoint.method && Objects.equals(
                parameters, endpoint.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, method, parameters);
    }
}

