package nextstep.jwp.web.network.request;

import nextstep.jwp.web.exception.InputException;
import nextstep.jwp.web.network.URI;

import java.io.BufferedReader;
import java.io.IOException;

public class RequestLine {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int PROTOCOL_VERSION_INDEX = 2;

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

    public static RequestLine of(BufferedReader bufferedReader) {
        try {
            final String[] requestLineElements = bufferedReader.readLine().split(" ");
            final String httpMethod = requestLineElements[HTTP_METHOD_INDEX];
            final URI uri = new URI(requestLineElements[URI_INDEX]);
            final String protocolVersion = requestLineElements[PROTOCOL_VERSION_INDEX];

            return new RequestLine(httpMethod, uri, protocolVersion);
        } catch (IOException exception) {
            throw new InputException("Exception while reading request line from http request");
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public URI getURI() {
        return uri;
    }
}
