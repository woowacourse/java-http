package org.apache.coyote.request;

import java.util.Map;

public class RequestHeader {

    private final RequestLine requestLine;
    private final Map<String, String> headers;

    public RequestHeader(RequestLine requestLine, Map<String, String> headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public String getHeaderBy(String key) {
        if (has(key)) {
            return headers.get(key);
        }
        throw new IllegalArgumentException("헤더 정보를 읽을 수 없습니다.");
    }

    public boolean has(String key) {
        return headers.containsKey(key);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
