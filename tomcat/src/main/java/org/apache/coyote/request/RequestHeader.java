package org.apache.coyote.request;

import java.util.Map;
import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpCookie;
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

    public boolean has(String key) {
        return headers.containsKey(key);
    }

    public Session getSession(boolean isCreate) {
        return getCookie().getSession(isCreate);
    }

    public HttpCookie getCookie() {
        if (has("Cookie")) {
            return HttpCookie.from(headers.get("Cookie"));
        }
        return HttpCookie.from("");
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Protocol getProtocol() {
        return requestLine.getProtocol();
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
