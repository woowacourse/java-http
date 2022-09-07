package org.apache.catalina;

import java.io.IOException;
import org.apache.catalina.session.Session;

public interface Manager {

    void add(Session session);

    Session findSession(String id) throws IOException;

    void remove(Session session);
}
