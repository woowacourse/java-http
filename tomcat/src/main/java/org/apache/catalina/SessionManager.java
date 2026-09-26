package org.apache.catalina;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 모든 클라이언트의 세션 값을 관리하는 클래스
// 각 세션의 id 는 uuid 사용
public class SessionManager implements Manager{

    // key = JSESSION 아이디값,value = Session
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(final Session session) {
        String id = session.getId();
        SESSIONS.put(id, session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    public boolean hasSessionId(Map<String, List<String>> headers) {
        return getSessionId(headers) != null;
    }

    private String getSessionId(Map<String, List<String>> requestHeaders) {
        List<String> cookieHeaders = requestHeaders.get("cookie");

        if (cookieHeaders == null) {
            return null;
        }

        for (String cookieHeader : cookieHeaders) {
            for (String cookie : cookieHeader.split(";")) {
                String[] parts = cookie.trim().split("=", 2);

                if (parts.length == 2 && parts[0].equals("JSESSIONID")) {
                    return parts[1].trim();
                }
            }
        }

        return null;
    }

    public Session getSession(Map<String, List<String>> requestHeaders, boolean create) {
        String sessionId = getSessionId(requestHeaders);

        if (sessionId != null) {
            Session session = findSession(sessionId);

            if (session != null) {
                return session;
            }
        }

        if (!create) {
            return null;
        }

        Session session = Session.create();
        add(session);
        return session;
    }

    public SessionManager() {}
}
