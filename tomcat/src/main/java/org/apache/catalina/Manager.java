package org.apache.catalina;

import org.apache.catalina.session.Session;

public interface Manager {

    void add(Session session);

    Session findSession(String id);
}
