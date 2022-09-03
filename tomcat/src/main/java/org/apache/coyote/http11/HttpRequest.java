package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;

    public HttpRequest(final String startLine) {
        this(startLine, new HashMap<>());
    }

    public HttpRequest(final String startLine, final Map<String, String> headers) {
        String[] splitStartLine = startLine.split(" ");
        this.requestLine = new RequestLine(splitStartLine[0], splitStartLine[1], splitStartLine[2]);
        this.headers = new Headers(new LinkedHashMap<>(headers));
    }

    public String getRequestLine() {
        return requestLine.getHttpMethod() + " " + getPath() + " " + requestLine.getHttpVersion();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        RequestURI requestURI = requestLine.getRequestURI();
        return requestURI.getPath();
    }

    public boolean existsQueryString() {
        RequestURI requestURI = requestLine.getRequestURI();
        return !requestURI.isQueryParametersEmpty();
    }

    public String getQueryParameter(final String key) {
        RequestURI requestURI = requestLine.getRequestURI();
        return requestURI.getQueryParameterKey(key);
    }

    public void addHeader(final String key, final String value) {
        headers.addHeader(key, value);
    }

    public String getHeader(final String key) {
        return headers.findByHeaderKey(key);
    }
}
