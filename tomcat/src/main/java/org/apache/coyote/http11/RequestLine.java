package org.apache.coyote.http11;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final String requestURI;

    private RequestLine(final HttpMethod httpMethod, final String requestURI) {
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
    }

    public static RequestLine from(final String line) {
        final String[] lines = line.split(" ");
        final var httpMethod =  HttpMethod.from(lines[0]);
        final String requestURI = lines[1];

        return new RequestLine(httpMethod, requestURI);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestURI() {
        return requestURI;
    }
}
