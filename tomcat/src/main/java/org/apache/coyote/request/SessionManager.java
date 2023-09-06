package org.apache.coyote.request;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

  private static final Map<String, Session> SESSIONS = new HashMap<>();

  @Override
  public void add(final Session session) {
    final UUID uuid = UUID.randomUUID();
    SESSIONS.put(uuid.toString(), session);
  }

  @Override
  public Session findSession(final String id) throws IOException {
    return SESSIONS.get(id);
  }

  @Override
  public void remove(final Session session) {
    SESSIONS.remove(session.getId());
  }
}
