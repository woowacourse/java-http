package org.apache.catalina.session;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {


    // 세션 풀
    private static final Map<String, HttpSessionImpl> SESSIONS = new ConcurrentHashMap<>();

    // 싱글턴
    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    /**
     * 세션 생성
     */
    public HttpSessionImpl createSession(ServletContext servletContext) {
        HttpSessionImpl session = new HttpSessionImpl(servletContext);
        add(session);
        return session;
    }

    @Override
    public HttpSession findSession(String id) throws IOException {
        if (id == null) {
            return null;
        }

        return SESSIONS.computeIfPresent(id, (k, session) -> {
            if (!session.isValid() || session.isExpired()) {
                session.invalidate();
                return null;
            }
            session.access(); // 접근 시간 갱신
            return session;
        });
    }

    @Override
    public void add(HttpSession session) {
        if (session instanceof HttpSessionImpl s) {
            SESSIONS.putIfAbsent(s.getId(), s);
        }
    }

    @Override
    public void remove(HttpSession session) {
        if (session instanceof HttpSessionImpl s) {
            SESSIONS.computeIfPresent(s.getId(), (k, v) -> {
                v.invalidate();
                return null; // null → remove
            });
        }
    }
}
