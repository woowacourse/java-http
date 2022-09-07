package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class Header {

    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CONTENT_LENGTH = "Content-Length: ";
    private static final String CONTENT_TYPE = "Content-Type: ";
    private static final String LOCATION = "Location: ";

    private final String requestLine;
    private final String headers;

    public Header(HttpStatus httpStatus, String url, String body) {
        this.requestLine = HTTP_VERSION + httpStatus.code;
        this.headers = createHeaders(url, body);
    }

    // TODO: 다양한 헤더 값에 적응할 수 있는 로직으로 대체
    public Header(HttpStatus httpStatus, String redirectUrl) {
        this.requestLine = HTTP_VERSION + httpStatus.code;
        this.headers = createHeadersForRedirect(redirectUrl);
    }

    private String createHeaders(String url, String body) {
        String contentType = getContentType(url);
        String contentLength = body.getBytes().length + " ";
        return String.join("\r\n",
                CONTENT_TYPE + contentType,
                CONTENT_LENGTH + contentLength,
                "");
    }

    private String createHeadersForRedirect(String redirectUrl) {
        return String.join("\r\n",
                LOCATION + redirectUrl,
                "");
    }

    private String getContentType(String requestUri) {
        return ContentType.find(requestUri) + ";charset=utf-8 ";
    }

    public String getHeader() {
        return String.join("\r\n", requestLine, headers);
    }

    public String getRequestLine() {
        return requestLine;
    }

    public String getHeaders() {
        return headers;
    }
}
