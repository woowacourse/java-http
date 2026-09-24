package org.apache.catalina.session;

public interface Manager {

    void add(Session session);

    Session findSession(final String id);

    void remove(final String id);
}
