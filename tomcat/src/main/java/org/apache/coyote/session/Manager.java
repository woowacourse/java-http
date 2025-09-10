package org.apache.coyote.session;

public interface Manager {

    void add(Session session);

    Session findSession(String id);

    void remove(Session session);
}
