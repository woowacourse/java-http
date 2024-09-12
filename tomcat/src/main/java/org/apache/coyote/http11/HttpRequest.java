package org.apache.coyote.http11;

import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.query.Query;

public class HttpRequest {

    private static final int START_LINE_INDEX = 0;
    private static final String DELIMITER = "\r\n";

    private HttpRequestStartLine startLine;
    private HttpHeaders httpHeaders;
    private Query body;

    public HttpRequest(HttpRequestStartLine startLine, HttpHeaders httpHeaders,
                       Query body) {
        this.startLine = startLine;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public static HttpRequest create(String raw) {
        String[] lines = raw.split(DELIMITER);
        HttpRequestStartLine startLine = HttpRequestStartLine.create(lines[START_LINE_INDEX]);
        HttpHeaders headers = new HttpHeaders();
        Query body = null;
        int i = 1;
        while (i < lines.length && !lines[i].isEmpty()) {
            headers.addByString(lines[i]);
            i++;
        }
        if (i + 1 < lines.length) {
            body = Query.create(lines[i + 1]);
        }

        return new HttpRequest(startLine, headers, body);
    }

    public boolean isSameMethod(HttpMethod method) {
        return startLine.isSameMethod(method);
    }

    public String findFromBody(String key) {
        return body.findByKey(key);
    }

    public boolean hasNoBody() {
        return body == null;
    }

    public String getPath() {
        return startLine.getPath();
    }

    public Session getSession(boolean create) {
        SessionManager sessionManager = SessionManager.getInstance();
        if (create) {
            return createSession(sessionManager);
        }
        Optional<String> sessionId = httpHeaders.findSessionId();
        return sessionId.map(sessionManager::findSession)
                .orElse(null);
    }

    private static Session createSession(SessionManager sessionManager) {
        String uuid = UUID.randomUUID().toString();
        Session session = new Session(uuid);
        sessionManager.add(session);
        return session;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "startLine=" + startLine +
                ", httpHeaders=" + httpHeaders +
                ", body='" + body + '\'' +
                '}';
    }
}
