package org.apache.catalina;

public interface Manager {

    void add(final Session session);

    Session findSession(final String id);

    void remove(final String id);
}
