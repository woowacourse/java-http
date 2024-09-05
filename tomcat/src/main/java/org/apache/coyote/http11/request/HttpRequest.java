package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    // 이후에 body 적용

    public HttpRequest(String requestLine, List<String> headers) {
        this.requestLine = RequestLine.from(requestLine);
        this.headers = parseHeaders(headers);
    }

    private Map<String, String> parseHeaders(List<String> headers) {
        return headers.stream()
                .map(header -> header.split(": "))
                .collect(Collectors.toMap(header -> header[0], header -> header[1]));
    }

    public Map<String, String> getQueryString() {
        return requestLine.getQueryString();
    }

    public String getExtension() {
        return requestLine.getExtension();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean isSameMethod(HttpMethod method) {
        return requestLine.getMethod().equals(method);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }
}
