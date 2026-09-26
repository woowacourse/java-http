package org.apache.catalina;

import java.util.Optional;

public interface Manager {

    void add(Session session);

    Optional<Session> findSession(String id);

    void remove(String id);
}
