package nextstep.jwp.web.network.request;

import nextstep.jwp.web.network.URI;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final URI uri;
    private final String protocolVersion;

    private RequestLine(String method, URI uri, String protocolVersion) {
        this(HttpMethod.of(method), uri, protocolVersion);
    }

    private RequestLine(HttpMethod httpMethod, URI uri, String protocolVersion) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine of(String line) {
        final String[] firstLineElements = line.split(" ");
        final String httpMethod = firstLineElements[0];
        final URI uri = new URI(firstLineElements[1]);
        final String protocolVersion = firstLineElements[2];

        return new RequestLine(httpMethod, uri, protocolVersion);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public URI getURI() {
        return uri;
    }
}
