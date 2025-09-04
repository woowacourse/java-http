package org.apache.coyote.httpObject;

import java.util.Map;
import java.util.TreeMap;

public class RequestLine {

    private final HttpMethod httpMethod;
    private final String path;
    private final String protocol;

    public RequestLine(final String requestLine) {
        final String trimmedRequestLine = requestLine.trim();
        this.httpMethod = HttpHeaderParser.findHttpMethod(trimmedRequestLine);
        this.path = HttpHeaderParser.findPath(trimmedRequestLine);
        this.protocol = HttpHeaderParser.findProtocol(trimmedRequestLine);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getQueryValues() {
        Map<String, String> values = new TreeMap<>();
        int index = path.indexOf("?");
        String queryPath = path.substring(index + 1);
        String[] queries = queryPath.split("&");
        for (String query : queries) {
            String[] split = query.split("=");
            values.put(split[0], split[1]);
        }
        return values;
    }
}
