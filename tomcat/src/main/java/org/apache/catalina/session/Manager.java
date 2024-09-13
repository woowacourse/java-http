package org.apache.catalina.session;

public interface Manager {

    void add(Session session);

    Session findSession(String sessionId);
}
