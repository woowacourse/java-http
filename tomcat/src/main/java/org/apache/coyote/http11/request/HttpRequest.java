package org.apache.coyote.http11.request;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers = new HashMap<>();

    public HttpRequest(final RequestLine requestLine) {
        this.requestLine = requestLine;
    }

    public static HttpRequest from(String requestLine) {
        String[] requestElements = requestLine.split(" ");

        Method method = Method.from(requestElements[0]);
        URI uri = URI.create(requestElements[1]);
        HttpProtocol protocol = HttpProtocol.from(requestElements[2]);

        return new HttpRequest(new RequestLine(method, uri, protocol));
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
        return requestLine.getMethod();
    }

    public URI getUri() {
        return requestLine.getUri();
    }

    public String getUriPath() {
        return requestLine.getUriPath();
    }
}
