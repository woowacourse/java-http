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
    private final SessionManager sessionManager;

    private Session newSession;

    public HttpRequest(
            final RequestLine requestLine,
            final RequestHeaders headers,
            final RequestBody body,
            final SessionManager sessionManager
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.sessionManager = sessionManager;
    }

    public static HttpRequest of(
            final RequestLine requestLine,
            final RequestHeaders headers,
            final RequestBody body,
            final SessionManager sessionManager
    ) {
        validateHostRequirement(requestLine, headers);
        return new HttpRequest(requestLine, headers, body, sessionManager);
    }

    private static void validateHostRequirement(final RequestLine requestLine, final RequestHeaders headers) {
        if (requestLine.getVersion() == HttpVersion.HTTP_1_1 && !headers.hasHost()) {
            throw new BadRequestException("HTTP/1.1 요청에는 Host 헤더가 필요합니다");
        }
    }

    public String getPath() {
        return requestLine.getPath().getValue();

    }

    public HttpCookie getCookie() {
        return headers.getCookie();
    }

    public Optional<Session> findSession() {
        if (newSession != null) {
            return Optional.of(newSession);
        }
        return getCookie().get(HttpCookie.JSESSIONID)
                .flatMap(sessionManager::findSession);
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

    public Session getSession() {
        return findSession().orElseGet(this::createSession);
    }

    public Optional<Session> getNewSession() {
        return Optional.ofNullable(newSession);
    }

    private Session createSession() {
        newSession = sessionManager.create();
        return newSession;
    }
}
