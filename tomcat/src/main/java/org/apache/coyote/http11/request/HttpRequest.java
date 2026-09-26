package org.apache.coyote.http11.request;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.request.requestline.HttpMethod;
import org.apache.coyote.http11.request.requestline.HttpVersion;
import org.apache.coyote.http11.request.requestline.RequestLine;

import java.util.Optional;

public class HttpRequest {
    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    public HttpRequest(
            final RequestLine requestLine,
            final RequestHeaders headers,
            final RequestBody body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(
            final RequestLine requestLine,
            final RequestHeaders headers,
            final RequestBody body
    ) {
        validateHostRequirement(requestLine, headers);
        return new HttpRequest(requestLine, headers, body);
    }

    private static void validateHostRequirement(final RequestLine requestLine, final RequestHeaders headers) {
        if (requestLine.getVersion() == HttpVersion.HTTP_1_1 && !headers.hasHost()) {
            throw new BadRequestException("HTTP/1.1 요청에는 Host 헤더가 필요합니다");
        }
    }

    public String getPath() {
        return requestLine.getPath().getValue();
    }

    public Optional<String> getQueryParameter(final String name) {
        return requestLine.getQueryParameter(name);
    }

    public Optional<String> getBodyParameter(final String name) {
        return body.getParameter(name);
    }

    public boolean isMethod(final HttpMethod method) {
        return requestLine.isMethod(method);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public HttpCookie getCookie() {
        return headers.getCookie();
    }
}
