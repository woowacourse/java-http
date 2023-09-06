package org.apache.coyote.http;

import org.apache.coyote.http.session.Session;

public interface LoginManager {

    void add(final Session session);

    boolean isAlreadyLogined(final String id);
}
