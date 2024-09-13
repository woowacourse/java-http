package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    private static final String DELIMITER_REQUEST_LINE = " ";

    private static final String DELIMITER_HEADER = ": ";

    private static final String DELIMITER_QUERY_STRING = "?";

    private static final String DELIMITER_PARAMETER_SET = "&";

    private static final String DELIMITER_PARAMETER_VALUE = "=";

    private static final String EMPTY_LINE = "";

    private static final int INVALID_QUERY_STRING_DELIMITER_INDEX = -1;

    private static final HttpRequestParser instance = new HttpRequestParser();

    private HttpRequestParser() {
    }

    public HttpRequest parseRequest(BufferedReader bufferedReader) throws IOException {
        String[] requestLine = bufferedReader.readLine().split(DELIMITER_REQUEST_LINE);
        HttpMethod httpMethod = parseHttpMethod(requestLine);
        HttpRequestPath httpRequestPath = parseHttpRequestPath(requestLine);
        QueryString queryString = parseQueryString(requestLine);
        HttpRequestHeaders httpRequestHeaders = parseHttpHeaders(bufferedReader);
        HttpRequestBody httpRequestBody = new HttpRequestBody(new HashMap<>());
        HttpCookie httpCookie = new HttpCookie(new HashMap<>());
        if (httpMethod.equals(HttpMethod.POST)) {
            httpRequestBody = parseHttpRequestBody(httpRequestHeaders, bufferedReader);
        }
        if (!httpRequestHeaders.getCookies().equals(EMPTY_LINE)) {
            httpCookie = HttpCookieExtractor.extractCookie(httpRequestHeaders);
        }
        return new HttpRequest(httpMethod, httpRequestPath, queryString,
                httpRequestHeaders, httpRequestBody, httpCookie);
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
        splitMultipleStrings(queryStrings, queryString.split(DELIMITER_PARAMETER_SET), DELIMITER_PARAMETER_VALUE);
        return new QueryString(queryStrings);
    }

    private int findQueryStringDelimiterIndex(String requestLine) {
        return requestLine.indexOf(DELIMITER_QUERY_STRING);
    }

    private HttpRequestHeaders parseHttpHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String headerLine = bufferedReader.readLine();
        while (!EMPTY_LINE.equals(headerLine)) {
            splitSingleString(headers, headerLine, DELIMITER_HEADER);
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
            splitMultipleStrings(requestBody,
                    new String(buffer).split(DELIMITER_PARAMETER_SET), DELIMITER_PARAMETER_VALUE);
        }
        return new HttpRequestBody(requestBody);
    }

    private void splitMultipleStrings(Map<String, String> set, String[] rawStrings, String delimiter) {
        for (String rawString : rawStrings) {
            splitSingleString(set, rawString, delimiter);
        }
    }

    private void splitSingleString(Map<String, String> set, String rawString, String delimiter) {
        int delimiterIndex = rawString.indexOf(delimiter);
        int delimiterLength = delimiter.length();
        String key = rawString.substring(0, delimiterIndex);
        String value = rawString.substring(delimiterIndex + delimiterLength);
        set.put(key, value);
    }

    public static HttpRequestParser getInstance() {
        return instance;
    }
}
