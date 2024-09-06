package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final HttpMethod httpMethod;
    private final String path;
    private final HttpVersion httpVersion;
    private final String requestBody;
    private final ContentType contentType;
    private final Integer contentLength;
    private final HttpRequestParameter httpRequestParameter;
    private final Map<String, String> cookies;

    public HttpRequest(HttpMethod httpMethod, String path, HttpVersion httpVersion, String requestBody,
                       ContentType contentType, Integer contentLength, HttpRequestParameter httpRequestParameter, Map<String, String> cookies) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
        this.requestBody = requestBody;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.httpRequestParameter = httpRequestParameter;
        this.cookies = new HashMap<>(cookies);
    }

    public String getSessionId() {
        return cookies.get("JSESSIONID");
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public Integer getContentLength() {
        return contentLength;
    }

    public HttpRequestParameter getHttpRequestParameter() {
        return httpRequestParameter;
    }
}
