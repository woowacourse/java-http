package org.apache.coyote.request;

import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.Protocol;

public class Request {

    private final RequestLine requestLine;
    private final ContentType contentType;

    public Request(RequestLine requestLine, ContentType contentType) {
        this.requestLine = requestLine;
        this.contentType = contentType;
    }

    public Protocol getProtocol() {
        return requestLine.getProtocol();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getResourceTypes() {
        return contentType.getValue();
    }

    public String getQueryStringValue(String key) {
        return requestLine.getQueryValue(key);
    }

    public boolean isSamePath(String urlPath) {
        return requestLine.isSamePath(urlPath);
    }

    public Map<String, String> getQueryString() {
        return requestLine.getQueryString();
    }
}
