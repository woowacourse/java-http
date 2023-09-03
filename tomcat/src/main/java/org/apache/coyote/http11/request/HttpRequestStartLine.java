package org.apache.coyote.http11.request;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpRequestStartLine {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String REQUEST_LINE_DELIMITER = " ";

    private final HttpMethod httpMethod;
    private final URI uri;
    private final String httpVersion;

    private HttpRequestStartLine(final HttpMethod httpMethod, final URI uri, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestStartLine of(String requestLine) {
        String[] requestLineParts = requestLine.split(REQUEST_LINE_DELIMITER);

        try {
            return new HttpRequestStartLine(
                    HttpMethod.valueOf(requestLineParts[HTTP_METHOD_INDEX]),
                    new URI(requestLineParts[REQUEST_URI_INDEX]),
                    requestLineParts[HTTP_VERSION_INDEX]
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public URI getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
