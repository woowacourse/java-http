package org.apache.coyote.http11.session;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;

import java.io.IOException;

public class SessionManager implements Manager {

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

    private SessionManager() {
    }
}
