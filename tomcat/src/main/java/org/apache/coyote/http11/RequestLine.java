package org.apache.coyote.http11;

import java.net.URI;

public class RequestLine {

    private static final String SP = " ";
    private static final int TOKEN_LENGTH = 3;

    private final HttpMethod method;
    private final URI uri;
    private final QueryParameters queryParameters;
    private final String version;

    public RequestLine(String requestLine) {
        String[] tokens = requestLine.split(SP);
        if (tokens.length != TOKEN_LENGTH) {
            throw new IllegalArgumentException("Request-Line should have 3 tokens");
        }
        this.method = HttpMethod.from(tokens[0]);
        this.uri = URI.create(tokens[1]);
        this.queryParameters = new QueryParameters(uri.getQuery());
        this.version = tokens[2];
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    public String getQueryParameter(String name) {
        return queryParameters.get(name);
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
               "method=" + method +
               ", uri=" + uri +
               ", queryParameters=" + queryParameters +
               ", version='" + version + '\'' +
               '}';
    }
}
