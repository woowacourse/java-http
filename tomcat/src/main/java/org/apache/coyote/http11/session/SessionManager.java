package org.apache.coyote.http11.session;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

	private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

	private SessionManager() {
	}

	public static void add(final Session session) {
		SESSIONS.put(session.getId(), session);
	}

	public static Session findSession(final String id) {
		return SESSIONS.get(id);
	}

	public static void clear() {
		SESSIONS.clear();
	}
}
