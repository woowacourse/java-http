package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private final Method method;
    private final String path;
    private final HttpVersion httpVersion;
    private final ContentType contentType;
    private final int contentLength;
    private final Map<String, String> queryParameter;
    private final Map<String, String> body;

    public HttpRequest(Method method,
                       String path,
                       HttpVersion httpVersion,
                       ContentType contentType,
                       int contentLength,
                       Map<String, String> queryParameter,
                       Map<String, String> body) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.queryParameter = queryParameter;
        this.body = body;
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

    public Map<String, String> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method +
                ", path='" + path + '\'' +
                ", httpVersion=" + httpVersion +
                ", contentType=" + contentType +
                ", contentLength=" + contentLength +
                ", queryParameter=" + queryParameter +
                ", body=" + body +
                '}';
    }
}
