package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {

    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void add(final Session session) {
        log.info("session add 완료: {}", session.getId());
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String id) {
        for (Session session : SESSIONS.values()) {
            log.info("로그인된 유저 파악할 때 찾은 SESSION의 JSESSION_ID = {}", session.getId());
        }
        return SESSIONS.get(id);
    }
}
