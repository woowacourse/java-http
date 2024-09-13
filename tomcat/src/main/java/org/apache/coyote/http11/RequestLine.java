package org.apache.coyote.http11;

import java.net.URI;

public class RequestLine {

    private static final String SP = " ";
    private static final int TOKEN_LENGTH = 3;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod method;
    private final URI uri;
    private final QueryParameters queryParameters;
    private final String version;

    public RequestLine(String requestLine) {
        String[] tokens = requestLine.split(SP);
        if (tokens.length != TOKEN_LENGTH) {
            throw new IllegalArgumentException("Request-Line should have 3 tokens");
        }
        this.method = HttpMethod.from(tokens[METHOD_INDEX]);
        this.uri = URI.create(tokens[URI_INDEX]);
        this.queryParameters = new QueryParameters(uri.getQuery());
        this.version = tokens[VERSION_INDEX];
    }

    public boolean hasPath(String path) {
        String requestLinePath = uri.getPath();
        return requestLinePath.equals(path);
    }

    public boolean hasMethod(HttpMethod httpMethod) {
        return method == httpMethod;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public String getQueryParameter(String name) {
        return queryParameters.get(name);
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
