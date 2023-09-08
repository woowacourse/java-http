package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private static final String ELEMENT_SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int PROTOCOL_VERSION_INDEX = 2;
    private static final String QUERY_STRING_INDICATOR = "?";
    private static final String QUERY_STRING_SEPARATOR = "&";

    private final HttpMethod httpMethod;
    private final String path;
    private final Map<String, String> queryStrings;
    private final String protocolVersion;

    private RequestLine(final HttpMethod httpMethod, final String path, final Map<String, String> queryStrings,
                       final String protocolVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryStrings = queryStrings;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine from(final String requestLine) {
        final String[] elements = requestLine.split(ELEMENT_SEPARATOR);
        final String uri = elements[URI_INDEX];
        String path = uri;
        String queryString = null;
        if(uri.contains(QUERY_STRING_INDICATOR)){
            int index = uri.indexOf(QUERY_STRING_INDICATOR);
            path = uri.substring(0, index);
            queryString = uri.substring(index + 1);
        }
        return new RequestLine(
                HttpMethod.from(elements[METHOD_INDEX]),
                path,
                parseQueryString(queryString),
                elements[PROTOCOL_VERSION_INDEX]
        );
    }

    private static Map<String, String> parseQueryString(final String string) {
        Map<String, String> queryStrings = new HashMap<>();
        if(string == null) {
            return queryStrings;
        }
        final String[] split = string.split(QUERY_STRING_SEPARATOR);
        for (String queryString : split) {
            final String[] keyAndValue = queryString.split("=");
            queryStrings.put(keyAndValue[0], keyAndValue[1]);
        }
        return queryStrings;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryStrings() {
        return queryStrings;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
