package org.apache.coyote.http11.auth;

public interface SessionRepository {

    void create(final Session session);

    Session getSession(final String id);

}
