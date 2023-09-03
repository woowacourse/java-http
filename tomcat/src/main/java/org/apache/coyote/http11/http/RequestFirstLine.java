package org.apache.coyote.http11.http;

import org.apache.coyote.http11.common.RequestMethod;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestFirstLine {

    private static final int VALID_ELEMENT_COUNT = 3;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final char QUERY_STRING_DELIMITER = '?';

    private final RequestMethod httpMethod;
    private final String requestUri;
    private final String httpVersion;
    private final Map<String, String> queryStrings;

    private RequestFirstLine(String httpMethod, String requestUri, String httpVersion, Map<String, String> queryStrings) {
        this.httpMethod = RequestMethod.find(httpMethod);
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
        this.queryStrings = queryStrings;
    }

    public static RequestFirstLine from(String requestFirstLine) {
        String[] requestLine = requestFirstLine.split(" ");
        if (requestLine.length != VALID_ELEMENT_COUNT) {
            throw new IllegalArgumentException("http 요청이 올바르지 않습니다.");
        }

        String requestMethod = requestLine[METHOD_INDEX];
        String requestUri = requestLine[URI_INDEX];
        String requestVersion = requestLine[HTTP_VERSION_INDEX];
        String queryStringValue = "";

        int queryStringIndex = requestUri.indexOf(QUERY_STRING_DELIMITER);
        if (queryStringIndex != -1) {
            queryStringValue = requestUri.substring(queryStringIndex + 1);
            requestUri = requestUri.substring(0, queryStringIndex);
        }

        Map<String, String> queryStrings = parseQueryStrings(queryStringValue);

        return new RequestFirstLine(
                requestMethod,
                requestUri,
                requestVersion,
                queryStrings);
    }

    private static Map<String, String> parseQueryStrings(String queryStringValue) {
        if (queryStringValue.equals("")) {
            return Map.of();
        }
        int queryStringBeginIndex = queryStringValue.indexOf(QUERY_STRING_DELIMITER);
        String queryString = queryStringValue.substring(queryStringBeginIndex + 1);
        String[] queryParameters = queryString.split("&");
        return Arrays.stream(queryParameters)
                .map(perQuery -> perQuery.split("="))
                .collect(Collectors.toMap(
                        queryParameter -> queryParameter[0],
                        queryParameter -> queryParameter[1]
                ));
    }

    public String getHttpMethod() {
        return httpMethod.name();
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getQueryStrings() {
        return queryStrings;
    }

    public String findQueryStringValue(String key) {
        if (!queryStrings.containsKey(key)) {
            throw new IllegalArgumentException("잘못된 key입니다.");
        }
        return queryStrings.get(key);
    }
}
