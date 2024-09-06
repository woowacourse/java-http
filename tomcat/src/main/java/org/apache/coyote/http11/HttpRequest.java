package org.apache.coyote.http11;

public class HttpRequest {
    private final HttpMethod httpMethod;
    private final String path;
    private final HttpVersion httpVersion;
    private final String requestBody;
    private final ContentType contentType;
    private final Integer contentLength;

    public HttpRequest(HttpMethod httpMethod, String path, HttpVersion httpVersion, String requestBody,
                       ContentType contentType, Integer contentLength) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
        this.requestBody = requestBody;
        this.contentType = contentType;
        this.contentLength = contentLength;
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
}
