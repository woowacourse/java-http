package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(String requestLine, Map<String, String> headers, String body) {
        this.requestLine = RequestLine.from(requestLine);
        this.requestHeaders = new RequestHeaders(headers);
        this.requestBody = RequestBody.from(body);
    }

    public static HttpRequest of(String requestLine, Map<String, String> headers, String body) {
        return new HttpRequest(requestLine, headers, body);
    }

    public boolean isExistQueryString() {
        return requestLine.isExistQueryString();
    }

    public String getQueryStringValue(String key) {
        return requestLine.getQueryStringValue(key);
    }

    public boolean containsUri(String uri) {
        return requestLine.containsUri(uri);
    }

    public String getBodyValue(String key) {
        return requestBody.getBodyValue(key);
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getRequestLine() {
        return requestLine.getRequestUri();
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }
}
