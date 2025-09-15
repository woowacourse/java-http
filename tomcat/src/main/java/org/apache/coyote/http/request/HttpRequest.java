package org.apache.coyote.http.request;

import static common.HttpConstants.CRLF;

import common.ContentType;
import common.HttpMethod;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class HttpRequest {

    private final HttpRequestLine requestLine;
    private final HttpRequestHeader header;
    private final HttpRequestBody body;

    public static HttpRequest from(
            final HttpRequestLine requestLine,
            final HttpRequestHeader header,
            final HttpRequestBody body
    ) {
        return new HttpRequest(requestLine, header, body);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public String getQueryParam(final String name) {
        return requestLine.getQueryParam(name);
    }

    public String getHeader(final String name) {
        return header.get(name);
    }

    public Map<String, String> getHeaders() {
        return header.asMap();
    }

    public String getCookie(final String name) {
        return header.getCookie(name);
    }

    public ContentType getContentType() {
        return header.getContentType();
    }

    public String getBodyParam(final String name) {
        return body.getValue(name);
    }

    @Override
    public String toString() {
        return "%s%s%s%s%s%s".formatted(requestLine, CRLF, header, CRLF, CRLF, body);
    }
}
