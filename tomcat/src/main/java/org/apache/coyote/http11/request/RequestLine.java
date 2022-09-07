package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpMethod;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int REQUEST_URI_INDEX = 1;
    private static final String QUERY_STRING_BEGIN_DELIMITER = "?";
    private static final String QUERY_STRING_KEY_VALUE_DELIMITER = "=";
    private static final String QUERY_STRING_UNIT_DELIMITER = "&";

    private final HttpMethod httpMethod;
    private final String requestUri;

    private RequestLine(HttpMethod httpMethod, String requestUri) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
    }

    public static RequestLine from(String requestLine) {
        return getRequestUri(requestLine);
    }

    private static RequestLine getRequestUri(String requestLine) {
        final List<String> parsedRequestLine = Arrays.stream(requestLine
                .split(REQUEST_LINE_DELIMITER))
                .collect(Collectors.toList());

        final HttpMethod httpMethod = HttpMethod.find(parsedRequestLine.get(0));
        final String requestUri = parsedRequestLine.get(REQUEST_URI_INDEX);
        return new RequestLine(httpMethod, requestUri);
    }

    private Map<String, String> getQueryString(String requestUri) {
        int index = requestUri.indexOf(QUERY_STRING_BEGIN_DELIMITER);
        final String queryString = requestUri.substring(index + 1);
        final String[] dividedQueryString = queryString.split(QUERY_STRING_UNIT_DELIMITER);

        return Arrays.stream(dividedQueryString)
                .map(query -> query.split(QUERY_STRING_KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(key -> key[0].trim(), value -> value[1].trim()));
    }

    public boolean isExistQueryString() {
        return requestUri.contains(QUERY_STRING_BEGIN_DELIMITER);
    }

    public String getQueryStringValue(String key) {
        final Map<String, String> queryStrings = getQueryString(requestUri);
        if (queryStrings.containsKey(key)) {
            return queryStrings.get(key);
        }
        return "";
    }

    public boolean containsUri(String uri) {
        return requestUri.contains(uri);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }
}
