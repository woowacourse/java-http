package org.apache.catalina;

import org.apache.coyote.http11.common.Session;

public interface Manager {

    void add(final Session session);

    Session findSession(final String id);

    void remove(final String id);
}
