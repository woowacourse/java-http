package org.apache.coyote.http11.request;

import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.HttpHeaders;
import org.apache.coyote.http11.header.HttpVersion;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpHeaders httpHeaders;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(final HttpRequestLine httpRequestLine, final HttpHeaders httpHeaders,
                       HttpRequestBody httpRequestBody) {
        this.httpRequestLine = httpRequestLine;
        this.httpHeaders = httpHeaders;
        this.httpRequestBody = httpRequestBody;
    }

    public boolean isSameMethod(final HttpMethod method) {
        return httpRequestLine.isSameMethod(method);
    }

    public String getRequestBodyValue(final String key) {
        return httpRequestBody.getValue(key);
    }

    public ContentType getContentType() {
        return ContentType.from(httpRequestLine.getPath());
    }

    public String getHttpPath() {
        return httpRequestLine.getPath();
    }

    public HttpVersion getHttpVersion() {
        return httpRequestLine.getVersion();
    }
}
