package org.apache.coyote.http11.session;

public interface Manager {

    void add(final Session session);

    Session findSession(final String session);
}
