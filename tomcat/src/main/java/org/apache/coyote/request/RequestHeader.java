package org.apache.coyote.request;

import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.Protocol;

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

    public int getContentLength() {
        if (has("Content-Length")) {
            return Integer.parseInt(headers.get("Content-Length"));
        }
        return 0;
    }

    public boolean hasQueryString() {
        return requestLine.hasQueryString();
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

    public String getPath() {
        return requestLine.getPath();
    }

    public Protocol getProtocol() {
        return requestLine.getProtocol();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getQueryValue(String key) {
        return requestLine.getQueryValue(key);
    }

    public boolean isSamePath(String otherPath) {
        return requestLine.isSamePath(otherPath);
    }

    public Map<String, String> getQueryString() {
        return requestLine.getQueryString();
    }

    public boolean isSameHttpMethod(HttpMethod otherHttpMethod) {
        return requestLine.isSameHttpMethod(otherHttpMethod);
    }
}
