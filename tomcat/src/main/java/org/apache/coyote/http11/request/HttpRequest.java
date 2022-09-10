package org.apache.coyote.http11.request;

import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.HttpHeaders;
import org.apache.coyote.http11.header.HttpVersion;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final HttpPath httpPath;
    private final HttpVersion httpVersion;
    private final HttpHeaders httpHeaders;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(final HttpRequestLine httpRequestLine, final HttpHeaders httpHeaders,
                       HttpRequestBody httpRequestBody) {
        this.httpMethod = httpRequestLine.getMethod();
        this.httpPath = httpRequestLine.getPath();
        this.httpVersion = httpRequestLine.getVersion();
        this.httpHeaders = httpHeaders;
        this.httpRequestBody = httpRequestBody;
    }

    public boolean isLoginRequest() {
        return httpPath.isLoginRequest();
    }

    public boolean isDefaultRequest() {
        return httpPath.isDefaultRequest();
    }

    public boolean isPostMethod() {
        return httpMethod.isPost();
    }

    public String getRequestBodyValue(final String key) {
        return httpRequestBody.getValue(key);
    }

    public ContentType getContentType() {
        return ContentType.from(httpPath.getPath());
    }

    public HttpPath getHttpPath() {
        return httpPath;
    }
}
