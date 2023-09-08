package org.apache.coyote.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

  private static final Map<String, Session> SESSIONS = new HashMap<>();

  @Override
  public void add(final Session session) {
    SESSIONS.put(session.getId(), session);
  }

  @Override
  public Session findSession(final String id) throws IOException {
    try {
      return SESSIONS.get(id);
    } catch (NullPointerException e) {
      throw new IllegalArgumentException("세션이 존재하지 않습니다.");
    }
  }

  @Override
  public void remove(final Session session) {
    SESSIONS.remove(session.getId());
  }
}
