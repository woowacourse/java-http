package org.apache.coyote.http.request;

import java.io.IOException;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Request {

    private static final Logger log = LoggerFactory.getLogger(Request.class);

    private static final Manager MANAGER = SessionManager.getInstance();

    private final RequestLine requestLine;

    private final RequestHeaders headers;

    private final RequestBody body;

    public Request(RequestLine requestLine, RequestHeaders headers, RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getQueryParamValue(String key) {
        return requestLine.getQueryParamValue(key);
    }

    public String getBodyValue(String key) {
        return body.getValue(key);
    }

    public boolean existQueryParams() {
        return requestLine.existQueryParams();
    }

    public Session getSession() {
        Session session = headers.findJSessionId()
                .map(this::getSession)
                .orElse(null);
        if (session != null) {
            return accessSession(session);
        }
        return MANAGER.createSession();
    }

    private Session getSession(String sessionId) {
        try {
            return MANAGER.findSession(sessionId);
        } catch (IOException e) {
            log.info("세션을 찾는 데 실패했습니다 id: {}", sessionId, e);
        }
        return null;
    }

    private Session accessSession(Session session) {
        if (session.isValid()) {
            session.access();
            return session;
        }
        MANAGER.remove(session);
        return null;
    }
}
