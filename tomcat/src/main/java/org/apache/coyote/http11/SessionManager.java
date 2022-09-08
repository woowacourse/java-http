package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    
    @Override
    public void add(final HttpSession session) {

    }

    @Override
    public HttpSession findSession(final String id) throws IOException {
        return null;
    }

    @Override
    public void remove(final HttpSession session) {

    }
}
