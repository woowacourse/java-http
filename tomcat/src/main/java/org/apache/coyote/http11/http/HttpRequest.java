package org.apache.coyote.http11.http;

import org.apache.coyote.http11.common.Cookie;
import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.common.RequestMethod;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private static final int VALID_ELEMENT_COUNT = 3;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final char QUERY_STRING_DELIMITER = '?';

    private final RequestMethod requestMethod;
    private final String endPoint;
    private final HttpVersion httpVersion;
    private final RequestData requestData;
    private final RequestHeader requestHeader;

    private HttpRequest(RequestMethod requestMethod, String endPoint, HttpVersion httpVersion, RequestData requestData, RequestHeader requestHeader) {
        this.requestMethod = requestMethod;
        this.endPoint = endPoint;
        this.httpVersion = httpVersion;
        this.requestData = requestData;
        this.requestHeader = requestHeader;
    }

    public static HttpRequest from(BufferedReader request) throws IOException {
        String[] requestLine = validateRequestFirstLine(request);

        RequestMethod requestMethod = RequestMethod.find(requestLine[METHOD_INDEX]);
        HttpVersion httpVersion = HttpVersion.find(requestLine[HTTP_VERSION_INDEX]);

        String requestUri = requestLine[URI_INDEX];
        String endPoint = parseEndPoint(requestUri);

        RequestHeader requestHeader = RequestHeader.from(request);
        String requestBody = parseRequestBody(request, requestHeader);
        RequestData requestData = RequestData.of(requestUri, requestBody);
        return new HttpRequest(
                requestMethod,
                endPoint,
                httpVersion,
                requestData,
                requestHeader
        );
    }

    private static String parseEndPoint(String requestUri) {
        int queryStringIndex = requestUri.indexOf(QUERY_STRING_DELIMITER);
        return extractEndPoint(requestUri, queryStringIndex);
    }

    private static String[] validateRequestFirstLine(BufferedReader request) throws IOException {
        String requestFirstLine = request.readLine();
        return validateRequestFirstLine(requestFirstLine);
    }

    private static String parseRequestBody(BufferedReader request, RequestHeader requestHeader) throws IOException {
        int contentLength = requestHeader.getContentLength();
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            request.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    public Cookie parseCookie() {
        return requestHeader.parseCookie();
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

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public RequestData getRequestData() {
        return requestData;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }
}
