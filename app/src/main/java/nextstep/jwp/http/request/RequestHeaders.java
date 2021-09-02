package nextstep.jwp.http.request;

import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;

import java.util.*;

public class RequestHeaders {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> headersExceptCookies;
    private final RequestCookie requestCookie;

    public RequestHeaders(Map<String, String> headersExceptCookies, RequestCookie requestCookie) {
        this.headersExceptCookies = headersExceptCookies;
        this.requestCookie = requestCookie;
    }

    public int getContentLength() {
        if (headersExceptCookies.containsKey(CONTENT_LENGTH)) {
            String value = headersExceptCookies.get(CONTENT_LENGTH);
            return Integer.parseInt(value);
        }
        return 0;
    }

    public RequestCookie getHttpCookie() {
        return requestCookie;
    }

    public HttpSession getSession() {
        if (requestCookie.containsKey(JSESSIONID)) {
            final String sessionId = requestCookie.get(JSESSIONID);
            return HttpSessions.getSession(sessionId)
                    .orElse(HttpSessions.createSession());
        }
        return HttpSessions.createSession();
    }

    public boolean isLoggedIn() {
        if (requestCookie.containsKey(JSESSIONID)) {
            return sessionHasUserAttribute();
        }
        return false;
    }

    private boolean sessionHasUserAttribute() {
        final String sessionId = requestCookie.get(JSESSIONID);
        final Optional<HttpSession> optionalSession = HttpSessions.getSession(sessionId);
        if (optionalSession.isEmpty()) {
            return false;
        }
        final HttpSession session = optionalSession.get();
        return session.hasAttribute("user");
    }
}
