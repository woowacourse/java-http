package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    private static final String DELIMITER_REQUEST_LINE = " ";

    private static final String DELIMITER_HEADER = ": ";

    private static final String DELIMITER_QUERY_STRING = "?";

    private static final String DELIMITER_QUERY_STRING_VALUES = "&";

    private static final String DELIMITER_QUERY_STRING_VALUE = "=";

    private static final String EMPTY_LINE = "";

    private static final int INVALID_QUERY_STRING_DELIMITER_INDEX = -1;

    public HttpRequest parseRequest(BufferedReader bufferedReader) throws IOException {
        String [] requestLine = bufferedReader.readLine().split(DELIMITER_REQUEST_LINE);
        HttpMethod httpMethod = parseHttpMethod(requestLine);
        HttpRequestPath httpRequestPath = parseHttpRequestPath(requestLine);
        QueryString queryString = parseQueryString(requestLine);
        HttpHeaders httpHeaders = parseHttpHeaders(bufferedReader);

        return new HttpRequest(httpMethod, httpRequestPath, queryString, httpHeaders);
    }

    private HttpMethod parseHttpMethod(String[] requestLine) {
        return HttpMethod.valueOf(requestLine[0]);
    }

    private HttpRequestPath parseHttpRequestPath(String[] requestLine) {
        String requestUri = requestLine[1];
        int queryStringDelimiterIndex = findQueryStringDelimiterIndex(requestUri);
        if (queryStringDelimiterIndex == INVALID_QUERY_STRING_DELIMITER_INDEX) {
            return new HttpRequestPath(requestLine[1]);
        }
        return new HttpRequestPath(requestUri.substring(0, queryStringDelimiterIndex));
    }

    private QueryString parseQueryString(String[] requestLine) {
        Map<String, String> queryStrings = new HashMap<>();
        String requestUri = requestLine[1];
        int queryStringDelimiterIndex = findQueryStringDelimiterIndex(requestUri);
        if (queryStringDelimiterIndex == INVALID_QUERY_STRING_DELIMITER_INDEX) {
            return new QueryString(queryStrings);
        }
        String queryString = requestUri.substring(queryStringDelimiterIndex + 1);
        String[] queryStringInfos = queryString.split(DELIMITER_QUERY_STRING_VALUES);
        for (String queryStringInfo : queryStringInfos) {
            String[] splittedQueryString = queryStringInfo.split(DELIMITER_QUERY_STRING_VALUE);
            String key = splittedQueryString[0];
            String value = splittedQueryString[1];
            queryStrings.put(key, value);
        }
        return new QueryString(queryStrings);
    }

    private int findQueryStringDelimiterIndex(String requestLine) {
        return requestLine.indexOf(DELIMITER_QUERY_STRING);
    }

    private HttpHeaders parseHttpHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String headerLine = bufferedReader.readLine();
        while(!EMPTY_LINE.equals(headerLine)) {
            String[] headerInfo = headerLine.split(DELIMITER_HEADER);
            String key = headerInfo[0];
            String value = headerInfo[1];
            headers.put(key, value);
            headerLine = bufferedReader.readLine();
        }
        return new HttpHeaders(headers);
    }
}
