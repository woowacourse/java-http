package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private final String method;
    private final String uri;
    private final Map<String, String> query;
    private final String protocol;

    public RequestLine(final String method, final String uri, final Map<String, String> query, final String protocol) {
        this.method = method;
        this.uri = uri;
        this.query = query;
        this.protocol = protocol;
    }

    public RequestLine(final String requestLine) {
        final String[] parts = requestLine.split(" ");
        final String path = parts[1];

        this.method = parts[0];
        this.uri = path.split("\\?")[0];
        this.query = parseQueryParams(path);
        this.protocol = parts[2].trim();
    }

    private Map<String, String> parseQueryParams(final String path) {
        final Map<String, String> queryParams = new HashMap<>();

        if (path.contains("?")) {
            final String queries = path.split("\\?")[1];
            queryParams.putAll(HttpRequestParser.parseParameters(queries));
        }
        return queryParams;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }
}
