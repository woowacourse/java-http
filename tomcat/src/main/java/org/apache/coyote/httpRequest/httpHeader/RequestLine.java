package org.apache.coyote.httpRequest.httpHeader;

import java.util.Map;
import java.util.TreeMap;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final String path;
    private final String protocol;

    public static RequestLine createErrorRequestLine(final String errorPath) {
        return new RequestLine(HttpMethod.GET, errorPath, "HTTP/1.1");
    }

    public RequestLine(final String requestLine) {
        final String trimmedRequestLine = requestLine.trim();
        this.httpMethod = HttpHeaderParser.findHttpMethod(trimmedRequestLine);
        this.path = HttpHeaderParser.findPath(trimmedRequestLine);
        this.protocol = HttpHeaderParser.findProtocol(trimmedRequestLine);
    }

    private RequestLine(
            final HttpMethod httpMethod,
            final String path,
            final String protocol
    ) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.protocol = protocol;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPurePath() {
        final int index = path.indexOf("?");
        if (index == -1) {
            return path;
        }
        return path.substring(0, index);
    }

    public Map<String, String> getQueryValues() {
        final Map<String, String> values = new TreeMap<>();
        final int index = path.indexOf("?");
        if (index == -1) {
            return values;
        }
        final String queryPath = path.substring(index + 1);
        final String[] queries = queryPath.split("&");
        for (final String query : queries) {
            final String[] split = query.split("=");
            values.put(split[0], split[1]);
        }
        return values;
    }
}
