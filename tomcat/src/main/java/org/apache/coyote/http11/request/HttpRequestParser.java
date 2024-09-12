package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.response.HttpResponseHeaders;

public class HttpRequestParser { // TODO : 매직넘버 제거 및 메서드 분리

    private static final String DELIMITER_REQUEST_LINE = " ";

    private static final String DELIMITER_HEADER = ": ";

    private static final String DELIMITER_QUERY_STRING = "?";

    private static final String DELIMITER_PARAMETER_SET = "&";

    private static final String DELIMITER_PARAMETER_VALUE = "=";

    private static final String EMPTY_LINE = "";

    private static final int INVALID_QUERY_STRING_DELIMITER_INDEX = -1;

    public HttpRequest parseRequest(BufferedReader bufferedReader) throws IOException {
        String[] requestLine = bufferedReader.readLine().split(DELIMITER_REQUEST_LINE);
        HttpMethod httpMethod = parseHttpMethod(requestLine);
        HttpRequestPath httpRequestPath = parseHttpRequestPath(requestLine);
        QueryString queryString = parseQueryString(requestLine);
        HttpRequestHeaders httpRequestHeaders = parseHttpHeaders(bufferedReader);
        HttpRequestBody httpRequestBody = new HttpRequestBody(new HashMap<>());
        if (httpMethod.equals(HttpMethod.POST)) {
            httpRequestBody = parseHttpRequestBody(httpRequestHeaders, bufferedReader);
        }

        return new HttpRequest(httpMethod, httpRequestPath, queryString, httpRequestHeaders, httpRequestBody);
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
        String[] queryStringInfos = queryString.split(DELIMITER_PARAMETER_SET);
        for (String queryStringInfo : queryStringInfos) { // TODO : 불용어 제거
            String[] splittedQueryString = queryStringInfo.split(DELIMITER_PARAMETER_VALUE);
            String key = splittedQueryString[0];
            String value = splittedQueryString[1];
            queryStrings.put(key, value);
        }
        return new QueryString(queryStrings);
    }

    private int findQueryStringDelimiterIndex(String requestLine) {
        return requestLine.indexOf(DELIMITER_QUERY_STRING);
    }

    private HttpRequestHeaders parseHttpHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String headerLine = bufferedReader.readLine();
        while (!EMPTY_LINE.equals(headerLine)) {
            int delimiterHeaderIndex = headerLine.indexOf(DELIMITER_HEADER);
            String key = headerLine.substring(0, delimiterHeaderIndex);
            String value = headerLine.substring(delimiterHeaderIndex + 2);
            headers.put(key, value);
            headerLine = bufferedReader.readLine();
        }
        return new HttpRequestHeaders(headers);
    }

    private HttpRequestBody parseHttpRequestBody(
            HttpRequestHeaders headers, BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestBody = new HashMap<>();
        int contentLength = headers.getContentLength();
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String[] requestBodySets = new String(buffer).split(DELIMITER_PARAMETER_SET);
            for (String requestBodySet : requestBodySets) {
                int delimiterIndex = requestBodySet.indexOf(DELIMITER_PARAMETER_VALUE);
                String key = requestBodySet.substring(0, delimiterIndex);
                String value = requestBodySet.substring(delimiterIndex + 1);
                requestBody.put(key, value);
            }
        }
        return new HttpRequestBody(requestBody);
    }
}
