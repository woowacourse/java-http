package org.apache.catalina.servletContainer.session;

public interface Manager {

    void add(final Session session);

    Session findSession(final String sessionId);

    void remove(final Session session);
}

