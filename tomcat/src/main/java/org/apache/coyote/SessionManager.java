package org.apache.coyote;

import org.apache.coyote.util.Cookie;

public interface SessionManager {

    Session findSession(String id);

    Session createSession();

    void add(Session session);

    void remove(String id);

    String getSessionId(Cookie cookie);
}
