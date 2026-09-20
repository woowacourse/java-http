package org.apache.coyote.http11;

import java.io.IOException;

public class HttpRequest {
    // 구성 요소
    // 헤더 map이 있고,
    // RequestLine을 가지고 있는다.
    private final RequestLine requestLine;

    public HttpRequest(String requestLine) throws IOException {
        this.requestLine = new RequestLine(requestLine);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    // RequestLine Method, Path, Protocol version
    // Headers
    // 공백
    // body
}
