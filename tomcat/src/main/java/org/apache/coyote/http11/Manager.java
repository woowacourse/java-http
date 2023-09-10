package org.apache.coyote.http11;

import org.apache.catalina.Session;

public interface Manager {

    void add(Session session);

    Session findSession(String id);
}
