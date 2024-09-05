package org.apache.coyote.request;

import java.util.Map;

public class Request {

    private final RequestLine requestLine;

    private final Map<String, String> headers; // todo object

    // todo body

    public Request(RequestLine requestLine, Map<String, String> headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getQueryParamValue(String key) {
        return requestLine.getQueryParamValue(key);
    }

    public String getHttpVersion() {
        return requestLine.getHttpVersion();
    }
}
