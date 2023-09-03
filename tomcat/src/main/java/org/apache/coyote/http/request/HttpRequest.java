package org.apache.coyote.http.request;

import org.apache.coyote.http.HttpHeader;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpHeader header;
    private final Map<String, String> parameters;

    public HttpRequest(HttpRequestLine requestLine, HttpHeader header, Map<String, String> parameters) {
        this.requestLine = requestLine;
        this.header = header;
        parameters.putAll(requestLine.getParameters());
        this.parameters = parameters;
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public Map<String, String> getParameters() {
        return new HashMap<>(parameters);
    }
}
