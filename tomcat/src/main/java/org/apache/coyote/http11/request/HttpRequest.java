package org.apache.coyote.http11.request;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final Method method;
    private final URI uri;
    private final String protocol;
    private final Map<String, String> headers = new HashMap<>();

    private HttpRequest(final Method method, final URI uri, final String protocol) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
    }

    public static HttpRequest from(String requestLine) {
        String[] requestElements = requestLine.split(" ");

        Method method = Method.from(requestElements[0]);
        URI uri = URI.create(requestElements[1]);
        String protocol = requestElements[2];

        return new HttpRequest(method, uri, protocol);
    }

    public void addHeader(String headerLine) {
        String[] headerParts = headerLine.split(": ");
        String key = headerParts[0];
        String value = headerParts[1];
        headers.put(key, value);
    }

    public String getHeaderValue(String key) {
        return headers.get(key);
    }

    public Method getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    public String getUriPath() {
        return uri.getPath();
    }
}
