package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestLine implements HttpComponent {

    private static final int METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod method;
    private final HttpRequestUri requestUri;
    private final HttpVersion version;

    public HttpRequestLine(final BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        String[] split = requestLine.split(SPACE);
        this.method = HttpMethod.from(split[METHOD_INDEX]);
        this.requestUri = new HttpRequestUri(split[REQUEST_URI_INDEX]);
        this.version = HttpVersion.from(split[VERSION_INDEX]);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpRequestUri getRequestUri() {
        return requestUri;
    }

    public String getPath() {
        return requestUri.getPath();
    }

    public String getQueryString() {
        return requestUri.getQueryString();
    }

    public HttpQueryParams getQueryParams() {
        return requestUri.getQueryParams();
    }

    public HttpVersion getVersion() {
        return version;
    }

    @Override
    public String asString() {
        return method.asString() + SPACE +
                requestUri.asString() + SPACE +
                version.asString() + SPACE;
    }
}
