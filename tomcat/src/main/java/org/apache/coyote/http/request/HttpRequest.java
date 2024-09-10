package org.apache.coyote.http.request;

import java.util.UUID;
import org.apache.coyote.http.Cookie;
import org.apache.coyote.http.session.Session;
import org.apache.coyote.http.session.SessionManager;

public class HttpRequest {

    private static final SessionManager sessionManager = new SessionManager();

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestParameters parameters;
    private final Cookie cookie;
    private final RequestBody body;
    private final Session session;

    public HttpRequest(RequestLine requestLine, RequestHeaders headers, RequestParameters parameters,
                       RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.cookie = parseCookie();
        this.body = body;
        this.parameters = parameters;
        this.session = parseSession();
    }

    private Cookie parseCookie() {
        String cookies = headers.getCookies();
        return new Cookie(cookies);
    }

    private Session parseSession() {
        return cookie.get("JSESSIONID").map(id -> {
            Session findSession = sessionManager.findSession(id);
            if (findSession != null) {
                return findSession;
            }
            Session session = new Session(id);
            sessionManager.add(session);
            return session;
        }).orElseGet(() -> {
            Session session = new Session(UUID.randomUUID().toString());
            sessionManager.add(session);
            return session;
        });
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public RequestBody getBody() {
        return body;
    }

    public String getParameter(String key) {
        return parameters.getValue(key);
    }

    public Session getSession() {
        return session;
    }

    @Override
    public String toString() {
        return "Request{" +
               "requestLine=" + requestLine +
               ", headers=" + headers +
               ", parameters=" + parameters +
               ", cookie=" + cookie +
               ", body='" + body + '\'' +
               ", session=" + session +
               '}';
    }
}
