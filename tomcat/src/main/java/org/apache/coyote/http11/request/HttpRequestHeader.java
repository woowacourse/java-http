package org.apache.coyote.http11.request;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpCookie;

public record HttpRequestHeader(
        HttpRequestLine httpRequestLine,
        HttpHeaders httpHeaders
) {

    public static HttpRequestHeader from(String firstLine, HttpHeaders httpHeaders) {
        String[] splitRequestLine = firstLine.split(" ");
        String method = splitRequestLine[0];
        HttpMethod httpMethod = HttpMethod.valueOf(method);
        String uri = splitRequestLine[1];
        String protocolVersion = splitRequestLine[2];
        HttpRequestLine httpRequestLine = new HttpRequestLine(httpMethod, uri, protocolVersion);
        return new HttpRequestHeader(httpRequestLine, httpHeaders);
    }

    public int getContentLength() {
        return httpHeaders.getContentLength();
    }

    public String getPath() {
        return httpRequestLine.getPath();
    }

    public String getQueryString() {
        return httpRequestLine.getQueryString();
    }

    public ContentType getContentType() {
        return httpHeaders.getContentType();
    }

    public String getJSessionId() {
        return httpHeaders.getJSessionId();
    }

    public HttpMethod getMethod() {
        return httpRequestLine.method();
    }

    public HttpCookie getHttpCookie() {
        return httpHeaders.getHttpCookie();
    }

    public String getProtocolVersion() {
        return httpRequestLine.protocolVersion();
    }

    public String getPathWithoutExtension() {
        return httpRequestLine.getPathWithoutExtension();
    }
}
