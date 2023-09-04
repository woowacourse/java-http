package org.apache.coyote.http;

public interface LoginManager {

    void add(final Session session);

    boolean isAlreadyLogined(final String id);
}
