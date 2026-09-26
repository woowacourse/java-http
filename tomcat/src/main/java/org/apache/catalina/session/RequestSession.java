package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RequestSession {

    private static final Logger log = LoggerFactory.getLogger(RequestSession.class);

    private final Manager manager;
    private HttpSession session;
    private String cookie;

    public RequestSession(Manager manager, String cookieHeader) throws IOException {
        this.manager = manager;
        this.session = manager.findSession(findSessionId(cookieHeader));
        if (session == null) {
            refreshCookie();
        }
    }

    public HttpSession current() {
        return session;
    }

    public HttpSession renew() {
        if (session != null) {
            try {
                session.invalidate();
            } catch (IllegalStateException e) {
                log.debug("이미 무효화된 세션입니다.");
            }
        }
        session = new Session(UUID.randomUUID().toString(), manager);
        manager.add(session);
        cookie = createCookie(session.getId());
        return session;
    }

    public void refreshCookie() {
        cookie = createCookie(UUID.randomUUID().toString());
    }

    public String cookie() {
        return cookie;
    }

    private String createCookie(String id) {
        return "JSESSIONID=" + id + "; Path=/";
    }

    private String findSessionId(String cookieHeader) {
        if (cookieHeader == null) {
            return null;
        }
        for (String cookie : cookieHeader.split(";")) {
            final String[] pair = cookie.trim().split("=", 2);
            if (pair.length == 2
                    && "JSESSIONID".equals(pair[0].trim())
                    && !pair[1].isBlank()) {
                return pair[1].trim();
            }
        }
        return null;
    }
}
