package org.apache.catalina.session;

public interface Manager {

    void add(final Session session);

    Session findSession(final String session);
}
