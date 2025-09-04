package org.apache.coyote.httpHeader;

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

    public String getPurePath() {
        int index = path.indexOf("?");
        if (index == -1) {
            return path;
        }
        return path.substring(0, index);
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
