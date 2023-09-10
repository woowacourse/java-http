package org.apache.catalina.core.servlet;

import static org.apache.coyote.http11.session.SessionManager.SESSION_ID_COOKIE_NAME;

import org.apache.coyote.http11.common.Method;
import org.apache.coyote.http11.common.QueryParameters;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.session.SessionManager.Session;

public class HttpServletRequest {

    private final Request request;
    private final SessionManager sessionManager;

    public HttpServletRequest(final Request request, final SessionManager sessionManager) {
        this.request = request;
        this.sessionManager = sessionManager;
    }

    public String getPath() {
        return request.getPath();
    }

    public Method getMethod() {
        return request.getMethod();
    }

    public String getUri() {
        return request.getUri();
    }

    public String getBody() {
        return request.getBody();
    }

    public String getQueryParamsForBody(final String key) {
        final var body = request.getBody();
        if (body == null) {
            return null;
        }

        final var queryParameters = QueryParameters.from(body);
        return queryParameters.findSingleByKey(key);
    }

    public Session getSession() {
        return sessionManager.findOrCreate(findSessionId());
    }

    private String findSessionId() {
        final var cookies = request.getCookies();

        return cookies.findByName(SESSION_ID_COOKIE_NAME);
    }

}
