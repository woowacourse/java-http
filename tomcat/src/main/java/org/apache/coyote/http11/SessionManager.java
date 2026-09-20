package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(HttpSession session) {

    }

    @Override
    public HttpSession findSession(String id) throws IOException {
        return null;
    }

    @Override
    public void remove(HttpSession session) {

    }
}
