package org.apache.coyote.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class HttpRequest {

    private final HttpRequestHeader header;
    private final HttpRequestBody body;

    public static HttpRequest from(final HttpRequestHeader header, final HttpRequestBody body) {
        return new HttpRequest(header, body);
    }

    public HttpMethod getMethod() {
        return header.getMethod();
    }

    public String getPath() {
        return header.getPath();
    }

    public String getVersion() {
        return header.getVersion();
    }

    public String getQueryParam(final String name) {
        return header.getQueryParam(name);
    }

    public String getHeader(final String name) {
        return header.getHeader(name);
    }

    public String getBodyParam(final String name) {
        return body.getValue(name);
    }

    public String getCookie(final String name) {
        return header.getCookie(name);
    }

    @Override
    public String toString() {
        return header + HttpConstants.CRLF + body;
    }
}
