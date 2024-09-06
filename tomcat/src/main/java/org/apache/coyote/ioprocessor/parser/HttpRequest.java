package org.apache.coyote.ioprocessor.parser;

import http.HttpMethod;
import http.HttpRequestBody;
import http.HttpRequestHeaders;
import http.HttpRequestLine;
import http.requestheader.Accept;
import java.util.Arrays;
import java.util.List;

public class HttpRequest {

    private static final String REQUEST_PART_DELIMITER = "\r\n\r\n";
    private static final String REQUEST_HEADER_DELIMITER = "\r\n";
    private final HttpRequestLine requestLine;
    private final HttpRequestHeaders requestHeaders;
    private final HttpRequestBody requestBody;

    public HttpRequest(String httpRequest) {
        String[] requestParts = httpRequest.split(REQUEST_PART_DELIMITER, -1);
        validateRequestPartsLength(requestParts);
        String[] requestHeaders = requestParts[0].split(REQUEST_HEADER_DELIMITER);
        this.requestLine = new HttpRequestLine(requestHeaders[0]);
        this.requestHeaders = new HttpRequestHeaders(buildRequestHeader(requestHeaders));
        this.requestBody = new HttpRequestBody(requestParts[1]);
    }

    private List<String> buildRequestHeader(String[] requestHeaders) {
        return Arrays.stream(requestHeaders)
                .skip(1)
                .toList();
    }

    private void validateRequestPartsLength(String[] requestParts) {
        if (requestParts.length < 2) {
            throw new RuntimeException("oh invalid protocol");
        }
    }

    public HttpRequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public String getQueryParam() {
        if (requestLine.getHttpMethod() == HttpMethod.GET) {
            return requestLine.getQueryParam();
        }
        return requestBody.getBody();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        return requestLine.getUri().getPath();
    }

    public String getMediaType() {
        Accept acceptValue = requestHeaders.getAcceptValue();
        return acceptValue.processContentType(getPath());
    }
}
