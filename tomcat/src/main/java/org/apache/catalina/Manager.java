package org.apache.catalina;

import nextstep.jwp.model.Session;

import java.util.Optional;

public interface Manager {

    void add(Session session);

    Optional<Session> findSession(String id);

    boolean containsSession(Session session);
}
