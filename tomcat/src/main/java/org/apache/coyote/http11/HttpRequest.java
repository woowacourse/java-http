package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;

    public HttpRequest(final String startLine) {
        this(startLine, new HashMap<>());
    }

    public HttpRequest(final String startLine, final Map<String, String> headers) {
        String[] splitStartLine = startLine.split(" ");
        this.requestLine = new RequestLine(splitStartLine[0], splitStartLine[1], splitStartLine[2]);
        this.headers = headers;
    }

    public void addHeader(final String key, final String value) {
        this.headers.put(key, value);
    }

    public String getRequestLine() {
        return requestLine.getHttpMethod() + " " + getPath() + " " + requestLine.getHttpVersion();
    }

    public String getHttpMethod() {
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

    public Map<String, String> getHeaders() {
        return headers;
    }
}
