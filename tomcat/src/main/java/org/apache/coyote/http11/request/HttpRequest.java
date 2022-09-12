package org.apache.coyote.http11.request;

import org.apache.coyote.http11.header.ContentType;
import org.apache.coyote.http11.header.HttpCookie;
import org.apache.coyote.http11.header.HttpVersion;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpRequestHeaders httpHeaders;
    private final HttpCookie httpCookie;
    private final HttpRequestBody httpRequestBody;

    public HttpRequest(final HttpRequestLine httpRequestLine,
                       final HttpRequestHeaders httpHeaders,
                       final HttpRequestBody httpRequestBody) {
        this.httpRequestLine = httpRequestLine;
        this.httpHeaders = httpHeaders;
        this.httpCookie = HttpCookie.from(httpHeaders.getCookie());
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

    public boolean hasCookie() {
        return httpCookie.hasCookie();
    }

    public String getHttpPath() {
        return httpRequestLine.getPath();
    }

    public HttpVersion getHttpVersion() {
        return httpRequestLine.getVersion();
    }

    public String getHttpMethod() {
        return httpRequestLine.getMethod().getValue();
    }

    public String getSessionId() {
        return httpCookie.getSessionId();
    }

    public String getHttpPathWithOutExtension() {
        return httpRequestLine.getPathWithOutExtension();
    }
}
