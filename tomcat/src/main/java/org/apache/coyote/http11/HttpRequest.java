package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private RequestLine requestLine;
    private Headers headers;
    private String body;
    private QueryParameters bodyParameters;

    public HttpRequest(final String startLine) {
        this(startLine, new HashMap<>());
    }

    public HttpRequest(final String startLine, final Map<String, String> headers) {
        String[] splitStartLine = startLine.split(" ");
        this.requestLine = new RequestLine(splitStartLine[0], splitStartLine[1], splitStartLine[2]);
        this.headers = new Headers(new LinkedHashMap<>(headers));
        this.body = "";
        this.bodyParameters = QueryParameters.EMPTY_QUERY_PARAMETERS;
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

    public String getQueryParameter(final String key) {
        RequestURI requestURI = requestLine.getRequestURI();
        return requestURI.getQueryParameterKey(key);
    }

    public boolean hasQueryParameter(final String... keys) {
        RequestURI requestURI = requestLine.getRequestURI();
        return requestURI.hasQueryParameter(keys);
    }

    public boolean isQueryParametersEmpty() {
        RequestURI requestURI = requestLine.getRequestURI();
        return requestURI.isQueryParametersEmpty();
    }

    public void addHeader(final String key, final String value) {
        headers.addHeader(key, value);
    }

    public void addBody(final String body) {
        this.body = body;
        if (body.contains("&") && body.contains("=")) {
            this.bodyParameters = new QueryParameters(body);
            return;
        }

        this.bodyParameters = QueryParameters.EMPTY_QUERY_PARAMETERS;
    }

    public String getBodyParameter(final String key) {
        return bodyParameters.findByQueryParameterKey(key);
    }

    public boolean hasBodyParameter(final String key) {
        return bodyParameters.hasQueryParameter(key);
    }
}
