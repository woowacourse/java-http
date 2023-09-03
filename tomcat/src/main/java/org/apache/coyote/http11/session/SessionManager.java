package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

	private static final Map<String, Session> SESSIONS = new HashMap<>();
	private static final SessionManager INSTANCE = new SessionManager();

	private SessionManager() {
	}

	public void add(final Session session) {
		SESSIONS.put(session.getId(), session);
	}

	public Session findSession(final String id) {
		return SESSIONS.get(id);
	}

	public void clear() {
		SESSIONS.clear();
	}

	public static SessionManager getInstance() {
		return INSTANCE;
	}
}
