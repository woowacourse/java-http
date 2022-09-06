package org.apache.coyote.http11.httpRequest;

import java.util.HashMap;

public class RequestLine {

    private static final String REQUEST_LINE_REGEX = " ";
    private static final int REQUEST_COMPONENT_LENGTH = 3;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private static final String QUERY_STRING_SEPARATOR = "?";
    private static final int EMPTY_QUERY_STRING_INDEX = -URI_INDEX;
    private static final int QUERY_STRING_INDEX = 1;

    private final HttpMethod method;
    private final String uri;
    private final String version;

    private RequestLine(HttpMethod method, String uri, String version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    private RequestLine(String... component) {
        this(HttpMethod.valueOf(component[METHOD_INDEX]), component[URI_INDEX], component[VERSION_INDEX]);
    }

    public static RequestLine from(String requestLine) {
        String[] components = requestLine.split(REQUEST_LINE_REGEX);
        if (components.length != REQUEST_COMPONENT_LENGTH) {
            throw new IllegalArgumentException("올바르지 않은 request line 입니다.");
        }
        return new RequestLine(components);
    }

    public String path() {
        int index = uri.indexOf(QUERY_STRING_SEPARATOR);
        if (index == EMPTY_QUERY_STRING_INDEX) {
            return uri;
        }
        return uri.substring(METHOD_INDEX, index);
    }

    public QueryString queryString() {
        String[] pathAndQueryString = uri.split("\\" + QUERY_STRING_SEPARATOR);
        if (pathAndQueryString.length <= QUERY_STRING_INDEX) {
            return new QueryString(new HashMap<>());
        }
        return QueryString.from(pathAndQueryString[QUERY_STRING_INDEX]);
    }

    public HttpMethod method() {
        return method;
    }
}
