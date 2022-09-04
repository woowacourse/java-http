package org.apache.catalina;

import java.io.IOException;

public interface Manager {

    void add(Session session);

    Session findSession(String id);

    void remove(Session session);
}
