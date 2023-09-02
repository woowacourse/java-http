package org.apache.coyote.http11.http;

public class RequestFirstLine {

    private static final int VALID_ELEMENT_COUNT = 3;

    private final String httpMethod;
    private final String requestUri;
    private final String httpVersion;

    private RequestFirstLine(String httpMethod, String requestUri, String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestFirstLine from(String requestFirstLine) {
        String[] requestLine = requestFirstLine.split(" ");
        if (requestLine.length != VALID_ELEMENT_COUNT) {
            throw new IllegalArgumentException("http 요청이 올바르지 않습니다.");
        }
        return new RequestFirstLine(
                requestLine[0],
                requestLine[1],
                requestLine[2]
        );
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
