package org.apache.catalina;

import java.util.Optional;

public interface Manager {

    Session createSession();

    Session renewSession(Session session);

    void add(Session session);

    Optional<Session> findSession(String id);

    void remove(String id);
}
