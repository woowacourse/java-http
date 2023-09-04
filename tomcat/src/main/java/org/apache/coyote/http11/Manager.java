package org.apache.coyote.http11;

public interface Manager {

    void add(Session session);

    Session findSession(String id);

    void remove(String id);
}
