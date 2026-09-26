package org.apache.coyote.http11;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
        validateRequestLine(requestLine);
        validateHeaders(headers);
        validateBody(body);
        this.requestLine = requestLine;
        this.headers = Map.copyOf(headers);
        this.body = body;
    }

    private void validateRequestLine(RequestLine requestLine) {
        if (requestLine == null) {
            throw new IllegalArgumentException("RequestLine은 null일 수 없습니다.");
        }
    }

    private void validateHeaders(Map<String, String> headers) {
        if (headers == null) {
            throw new IllegalArgumentException("HTTP 요청 헤더는 null일 수 없습니다.");
        }

        for (Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException("HTTP 요청 헤더 이름은 null일 수 없습니다.");
            }
            if (entry.getValue() == null) {
                throw new IllegalArgumentException("HTTP 요청 헤더 값은 null일 수 없습니다.");
            }
        }
    }

    private void validateBody(String body) {
        if (body == null) {
            throw new IllegalArgumentException("HTTP 요청 body는 null일 수 없습니다.");
        }
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getQueryString() {
        return requestLine.getQueryString();
    }

    public String getHeader(String name) {
        if (name == null) {
            throw new IllegalArgumentException("HTTP 요청 헤더 이름은 null일 수 없습니다.");
        }

        return headers.get(name.toLowerCase(Locale.ROOT));
    }
}
