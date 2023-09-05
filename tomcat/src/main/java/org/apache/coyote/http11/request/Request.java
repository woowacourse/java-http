package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.SessionManager.SESSION_ID_COOKIE_NAME;

import java.net.URI;
import java.util.Optional;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.Method;
import org.apache.coyote.http11.common.Session;

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

    public static Request from(
            final String methodName,
            final String requestURI,
            final Headers headers,
            final String body
    ) {
        final Method method = Method.find(methodName)
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

    public Session getOrCreateSession() {
        return findSession()
                .orElseGet(this::createSession);
    }

    private Session createSession() {
        final Session session = new Session();
        SESSION_MANAGER.add(session);
        return session;
    }

    private Optional<Session> findSession() {
        final Cookies cookies = headers.getCookie();
        final String sessionId = cookies.findByName(SESSION_ID_COOKIE_NAME);
        return Optional.ofNullable(SESSION_MANAGER.findSession(sessionId));
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
