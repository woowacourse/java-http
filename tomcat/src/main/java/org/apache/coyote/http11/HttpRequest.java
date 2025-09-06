package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private final Method method;
    private final String path;
    private final HttpVersion httpVersion;
    private final ContentType contentType;
    private final Map<String, String> queryParameter;

    public HttpRequest(Method method,
                       String path,
                       HttpVersion httpVersion,
                       ContentType contentType,
                       Map<String, String> queryParameter) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
        this.contentType = contentType;
        this.queryParameter = queryParameter;
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getQueryParameterValue(String key) {
        String value = queryParameter.get(key);
        if (value == null || value.isBlank()) {
            return "";
        }
        return value;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method +
                ", path='" + path + '\'' +
                ", httpVersion=" + httpVersion +
                ", contentType=" + contentType +
                ", queryParameter=" + queryParameter +
                '}';
    }
}
