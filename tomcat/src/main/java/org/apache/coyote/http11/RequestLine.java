package org.apache.coyote.http11;

import java.net.URI;
import javax.annotation.Nonnull;


public class RequestLine {
    private final HttpMethod method;
    private final URI uri;
    private final String protocol;
    private final QueryParameters queryParameters;

    private RequestLine(HttpMethod method, URI uri, String protocol, QueryParameters queryParameters) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
        this.queryParameters = queryParameters;
    }


    public static RequestLine from(String requestLine) {
        final String[] requestLineParts = parseRequestLine(requestLine);
        final HttpMethod method = HttpMethod.parse(requestLineParts[0]);
        final URI uri = createURI(requestLineParts[1]);
        final QueryParameters queryParameters = QueryParameters.from(uri.getRawQuery());
        final String protocol = requestLineParts[2];

        return new RequestLine(method, uri, protocol, queryParameters);
    }

    @Nonnull
    private static String[] parseRequestLine(String requestLine) {
        final String[] requestLineParts = requestLine.split(" ", 3);
        if (requestLineParts.length != 3) {
            throw new InvalidHttpRequestException("Invalid request line: " + requestLine);
        }
        return requestLineParts;
    }

    @Nonnull
    private static URI createURI(String uri) {
        try {
            return URI.create(uri);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpRequestException("Invalid URI: " + uri);
        }
    }

    public String getMethod() {
        return method.name();
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getProtocol() {
        return protocol;
    }

    public String getParameter(String name) {
        return queryParameters.get(name)
                .orElse(null);
    }

    public boolean matches(String method, String path) {
        return this.method.equals(method) && this.uri.getPath().equals(path);
    }
}
