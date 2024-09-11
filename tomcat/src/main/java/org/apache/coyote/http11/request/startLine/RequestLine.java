package org.apache.coyote.http11.request.startLine;

import static org.apache.coyote.http11.Constants.REQUEST_LINE_DELIMITER;

import org.apache.coyote.http11.response.header.ContentType;

public class RequestLine {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int VALID_PARTS_LENGTH = 3;

    private final HttpMethod httpMethod;
    private final String uri;
    private final HttpVersion httpVersion;

    public RequestLine(String requestLine) {
        validateRequestLine(requestLine);

        String[] requestParts = splitRequestLine(requestLine);

        this.httpMethod = HttpMethod.find(requestParts[HTTP_METHOD_INDEX]);
        this.uri = requestParts[URI_INDEX];
        this.httpVersion = new HttpVersion(requestParts[HTTP_VERSION_INDEX]);
    }

    private void validateRequestLine(String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("요청 라인은 비어있거나 공백일 수 없습니다.");
        }
    }

    private String[] splitRequestLine(String requestLine) {
        String[] requestParts = requestLine.split(REQUEST_LINE_DELIMITER);
        if (requestParts.length != VALID_PARTS_LENGTH) {
            throw new IllegalArgumentException("올바르지 않은 요청 라인 구조입니다.");
        }
        return requestParts;
    }

    public boolean isStaticResource() {
        return ContentType.isStaticResource(uri);
    }

    public boolean isMethod(HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
