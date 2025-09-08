package org.apache.catalina.requestMapping.session;

public interface Manager {

    void add(final Session session);

    Session findSession(final String sessionId);

    void remove(final Session session);
}

