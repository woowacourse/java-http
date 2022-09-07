package org.apache.coyote.http11.session;

public interface Manager {

    void add(Session session);

    Session findSession(String session);

    void remove(String id);
}
