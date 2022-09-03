package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private RequestLine requestLine;
    private Headers headers;
    private String body;
    private QueryParameters requestParameters;

    public HttpRequest(final String startLine) {
        this(startLine, new HashMap<>());
    }

    public HttpRequest(final String startLine, final Map<String, String> headers) {
        String[] splitStartLine = startLine.split(" ");
        this.requestLine = new RequestLine(splitStartLine[0], splitStartLine[1], splitStartLine[2]);
        this.headers = new Headers(new LinkedHashMap<>(headers));
        this.body = "";
        this.requestParameters = QueryParameters.EMPTY_QUERY_PARAMETERS;
    }

    public String getRequestLine() {
        return requestLine.getHttpMethod() + " " + getPath() + " " + requestLine.getHttpVersion().getValue();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        RequestURI requestURI = requestLine.getRequestURI();
        return requestURI.getPath();
    }

    public void addHeader(final String key, final String value) {
        headers.addHeader(key, value.trim());
    }

    public boolean hasHeader(final String key) {
        return headers.hasHeader(key);
    }

    public String getHeader(final String key) {
        return headers.getHeader(key);
    }

    public void addBody(final String body) {
        this.body = body;
        if (body.contains("&") && body.contains("=")) {
            this.requestParameters = new QueryParameters(body);
            return;
        }

        this.requestParameters = QueryParameters.EMPTY_QUERY_PARAMETERS;
    }

    public String getBodyParameter(final String key) {
        return requestParameters.findByQueryParameterKey(key);
    }

    public boolean hasBodyParameter(final String... keys) {
        return requestParameters.hasQueryParameter(keys);
    }
}
