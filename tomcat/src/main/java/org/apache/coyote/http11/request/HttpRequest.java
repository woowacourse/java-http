package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Optional;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody body;
    private final RequestCookie cookies;

    public HttpRequest(final RequestLine requestLine, final RequestHeader header, final RequestBody body,
                       final RequestCookie cookies) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
        this.cookies = cookies;
    }

    public Session generateSession(final Map<String, Object> attributes) {
        final Session session = generateSession();
        session.setAttributes(attributes);
        return session;
    }

    private Session generateSession() {
        final Session session = Session.generate();
        SessionManager.add(session);
        return session;
    }

    public RequestMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean existSession() {
        return Optional.ofNullable(cookies.getSessionId())
                .map(SessionManager::findSession)
                .isPresent();
    }

    public Params getParamsFromBody() {
        return body.getParams();
    }
}
