package org.apache.catalina;

/** Manages sessions by their IDs. */
public interface Manager {

    void add(Session session);

    Session findSession(String id);

    void remove(String id);
}
