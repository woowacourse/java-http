package org.apache.coyote.http11;

import java.net.URI;

public class HttpRequest {

    private final Method method;
    private final URI uri;

    public HttpRequest(Method method, URI uri) {
        this.method = method;
        this.uri = uri;
    }

    public static HttpRequest from(String requestLine) {
        String[] requestElements = requestLine.split(" ");

        Method method = Method.from(requestElements[0]);
        URI uri = URI.create(requestElements[1]);

        return new HttpRequest(method, uri);
    }

}
