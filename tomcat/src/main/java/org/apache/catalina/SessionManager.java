package org.apache.catalina;

import jakarta.servlet.http.HttpSession;
import org.apache.coyote.http11.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager{

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

    private SessionManager() {}
}
