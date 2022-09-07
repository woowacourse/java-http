package org.apache.coyote.http11;

import java.net.URI;
import org.apache.coyote.exception.InvalidHttpRequestFormatException;

public class HttpRequestStartLine {

    private static final int VALID_START_LINE_SIZE = 3;
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;

    private final HttpMethod httpMethod;
    private final URI uri;
    private final String protocol;

    private HttpRequestStartLine(final HttpMethod httpMethod, final URI uri, final String protocol) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.protocol = protocol;
    }

    public static HttpRequestStartLine parse(final String line) {
        String[] parsedStartLine = line.split(" ");
        if (parsedStartLine.length != VALID_START_LINE_SIZE) {
            throw new InvalidHttpRequestFormatException();
        }
        String rawMethod = parsedStartLine[HTTP_METHOD_INDEX];
        String rawUri = parsedStartLine[URL_INDEX];
        String protocol = parsedStartLine[PROTOCOL_INDEX];
        return new HttpRequestStartLine(HttpMethod.from(rawMethod), URI.create(rawUri), protocol);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public URI getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }
}
