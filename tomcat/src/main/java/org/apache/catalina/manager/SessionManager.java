package org.apache.catalina.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.domain.Session;
import org.apache.catalina.domain.cookie.HttpCookie;
import org.apache.catalina.domain.cookie.HttpCookies;
import org.apache.catalina.domain.request.HttpRequest;
import org.apache.catalina.domain.response.HttpResponse;

public class SessionManager implements Manager {

    public static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    public Session createSession() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        add(session);
        return session;
    }

    public Session findSessionById(String id) {
        return SESSIONS.get(id);
    }

    public static void processSessionId(HttpRequest request, HttpResponse httpResponse) {
        // 정적 파일 요청에는 세션 생성하지 않음
        String requestPath = request.requestStartLine().path();
        if (isStaticResource(requestPath)) {
            return;
        }

        final HttpCookies cookies = request.header().getCookies();

        if (cookies.hasCookie(SESSION_COOKIE_NAME)) {
            processExistSession(request, httpResponse, cookies);
            return;
        }

        // 쿠키가 없는 경우 새로운 세션 생성
        SessionManager sessionManager = new SessionManager();
        Session session = sessionManager.getSession(request, true);
        httpResponse.addSetCookie(new HttpCookie(SESSION_COOKIE_NAME, session.getId()));
    }

    private static void processExistSession(HttpRequest request, HttpResponse httpResponse, HttpCookies cookies) {
        String existingSessionId = cookies.getCookie(SESSION_COOKIE_NAME).value();
        SessionManager sessionManager = new SessionManager();

        // 기존 쿠키의 세션 ID가 유효한지 확인
        if (sessionManager.findSessionById(existingSessionId) != null) {
            return;
        }

        // 유효하지 않은 세션 ID이므로 새로운 세션 생성하고 쿠키 덮어쓰기
        Session newSession = sessionManager.getSession(request, true);
        httpResponse.addSetCookie(new HttpCookie(SESSION_COOKIE_NAME, newSession.getId()));
    }

    private static boolean isStaticResource(String path) {
        return path.endsWith(".css") || path.endsWith(".js");
    }

    public static String extractSessionIdFromCookie(HttpCookies cookies) {
        if (cookies.hasCookie(SESSION_COOKIE_NAME)) {
            return cookies.getCookie(SESSION_COOKIE_NAME).value();
        }
        return null;
    }

    public Session getSession(HttpRequest request, boolean create) {
        String sessionId = extractSessionIdFromCookie(request.header().getCookies());

        if (sessionId == null) {
            return createIfRequested(create);
        }

        Session session = findSessionById(sessionId);
        if (session != null) {
            return session;
        }

        return createIfRequested(create);
    }

    private Session createIfRequested(boolean create) {
        if (create) {
            return createSession();
        }
        return null;
    }
}
