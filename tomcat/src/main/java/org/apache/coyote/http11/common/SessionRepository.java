package org.apache.coyote.http11.common;

public interface SessionRepository {

    void create(final Session session);

    Session getSession(final String id);

    void remove(final String id);

}
