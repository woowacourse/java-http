package org.apache.coyote.request;

import java.util.Map;

public class RequestLine {

    public static final String REQUEST_LINE_SEPARATOR = " ";
    public static final String QUERY_PARAM_SEPARATOR = "?";
    public static final int HTTP_METHOD_INDEX = 0;
    public static final int PATH_INDEX = 1;
    public static final int NOT_EXIST = -1;

    private final HttpMethod httpMethod;
    private final String requestUri;
    private final QueryParams queryParams;

    private RequestLine(final HttpMethod httpMethod, final String requestUri, final QueryParams queryParams) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.queryParams = queryParams;
    }

    public static RequestLine from(final String requestLine) {
        String[] tokens = requestLine.split(REQUEST_LINE_SEPARATOR);
        String path = tokens[PATH_INDEX];
        int index = path.indexOf(QUERY_PARAM_SEPARATOR);
        if (index == NOT_EXIST) {
            return new RequestLine(HttpMethod.of(tokens[HTTP_METHOD_INDEX]), path, QueryParams.from(null));
        }
        String requestUri = path.substring(0, index);
        String parameter = path.substring(index + 1);
        return new RequestLine(HttpMethod.of(tokens[HTTP_METHOD_INDEX]), requestUri, QueryParams.from(parameter));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public Map<String, String> getQueryParams() {
        return queryParams.getValue();
    }
}
