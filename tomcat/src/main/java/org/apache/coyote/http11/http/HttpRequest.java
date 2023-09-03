package org.apache.coyote.http11.http;

import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.common.RequestMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private static final int VALID_ELEMENT_COUNT = 3;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final char QUERY_STRING_DELIMITER = '?';

    private final RequestMethod requestMethod;
    private final String endPoint;
    private final HttpVersion httpVersion;
    private final Map<String, String> queryStrings;
    private final RequestHeader requestHeader;

    private HttpRequest(RequestMethod requestMethod, String endPoint, HttpVersion httpVersion, Map<String, String> queryStrings, RequestHeader requestHeader) {
        this.requestMethod = requestMethod;
        this.endPoint = endPoint;
        this.httpVersion = httpVersion;
        this.queryStrings = queryStrings;
        this.requestHeader = requestHeader;
    }

    public static HttpRequest from(BufferedReader request) throws IOException {
        String requestFirstLine = request.readLine();
        String[] requestLine = validateRequestFirstLine(requestFirstLine);

        RequestMethod requestMethod = RequestMethod.find(requestLine[METHOD_INDEX]);
        String requestUri = requestLine[URI_INDEX];
        HttpVersion httpVersion = HttpVersion.find(requestLine[HTTP_VERSION_INDEX]);

        int queryStringIndex = requestUri.indexOf(QUERY_STRING_DELIMITER);
        String endPoint = extractEndPoint(requestUri, queryStringIndex);
        Map<String, String> queryStrings = extractQueryStrings(requestUri, queryStringIndex);

        RequestHeader requestHeader = RequestHeader.from(request);
        int contentLength = requestHeader.getContentLength();
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            request.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);
            queryStrings = parseQueryStrings(requestBody);
        }
        return new HttpRequest(
                requestMethod,
                endPoint,
                httpVersion,
                queryStrings,
                requestHeader
        );
    }

    private static String[] validateRequestFirstLine(String requestFirstLine) {
        String[] requestLine = requestFirstLine.split(" ");
        if (requestLine.length != VALID_ELEMENT_COUNT) {
            throw new IllegalArgumentException("http 요청이 올바르지 않습니다.");
        }
        return requestLine;
    }

    private static String extractEndPoint(String requestUri, int queryStringIndex) {
        if (queryStringIndex == -1) {
            return requestUri;
        }
        return requestUri.substring(0, queryStringIndex);
    }

    private static Map<String, String> extractQueryStrings(String requestUri, int queryStringIndex) {
        if (queryStringIndex == -1) {
            return Map.of();
        }
        String queryStringValue = requestUri.substring(queryStringIndex + 1);
        return parseQueryStrings(queryStringValue);
    }

    private static Map<String, String> parseQueryStrings(String queryStringValue) {
        int queryStringBeginIndex = queryStringValue.indexOf(QUERY_STRING_DELIMITER);
        String queryString = queryStringValue.substring(queryStringBeginIndex + 1);
        String[] queryParameters = queryString.split("&");

        return Arrays.stream(queryParameters)
                .map(perQuery -> perQuery.trim().split("="))
                .collect(Collectors.toMap(
                        queryParameter -> queryParameter[0],
                        queryParameter -> queryParameter[1]
                ));
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getQueryStrings() {
        return queryStrings;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }
}
