package org.apache.coyote.http;

import java.io.IOException;
import java.util.StringJoiner;

import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class HttpRequest implements HttpComponent {

    private final HttpRequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(final String httpRequest) throws IOException {
        this.requestLine = new HttpRequestLine(httpRequest);
        this.headers = new HttpHeaders(httpRequest);
        String[] parts = httpRequest.split("\r\n\r\n", 2);
        this.body = new HttpBody(parts[1]);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpQueryParams getQueryParams() {
        return requestLine.getQueryParams();
    }

    public HttpVersion getHttpVersion() {
        return requestLine.getVersion();
    }

    public String getAccept() {
        return headers.get(HttpHeaders.ACCEPT);
    }

    public HttpCookies getCookies() {
        return headers.getCookies();
    }

    public Session getSession() {
        return getSession(true);
    }

    public Session getSession(boolean create) {
        Manager manager = SessionManager.getInstance();
        String sessionId = headers.getSessionId();
        Session session = null;
        if (sessionId != null) {
            session = manager.findSession(sessionId);
        }
        if (create && session == null) {
            session = new Session();
            manager.add(session);
        }
        return session;
    }

    public HttpBody getBody() {
        return body;
    }

    @Override
    public String asString() {
        final var result = new StringJoiner(LINE_FEED);
        return result.add(requestLine.asString())
                .add(headers.asString())
                .add(body.asString())
                .toString();
    }
}
