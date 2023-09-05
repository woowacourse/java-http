package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.SessionManager.SESSION_ID_COOKIE_NAME;

import java.net.URI;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.SessionManager.Session;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.Method;

public class Request {

    private static final SessionManager SESSION_MANAGER = new SessionManager();
    private final Method method;
    private final String uri;
    private final Headers headers;
    private final String body;

    private Request(
            final Method method,
            final String uri,
            final Headers headers,
            final String body
    ) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.body = body;
    }

    public static Request of(
            final String methodName,
            final String requestURI,
            final Headers headers,
            final String body
    ) {
        final var method = Method.find(methodName)
                .orElseThrow(() -> new IllegalArgumentException("invalid method"));

        return new Request(method, requestURI, headers, body);
    }

    public String getPath() {
        return URI.create(uri).getPath();
    }

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public Session getSession() {
        return SESSION_MANAGER.findOrCreate(findSessionId());
    }

    private String findSessionId() {
        final var cookies = headers.getCookie();

        return cookies.findByName(SESSION_ID_COOKIE_NAME);
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "method=" + method +
                ", URI='" + uri + '\'' +
                ", headers='" + headers + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

}
