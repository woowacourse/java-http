package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

	private static final Map<String, Session> SESSIONS = new HashMap<>();

	private SessionManager() {
	}

	public static SessionManager getInstance(){
		return new SessionManager();
	}

	public static void add(final Session session) {
		SESSIONS.put(session.getId(), session);
	}

	public static boolean hasSession(final String id) {
		return SESSIONS.containsKey(id);
	}

	public static Session findSession(final String id) {
		return SESSIONS.get(id);
	}

	public static void remove(final String id) {
		SESSIONS.remove(id);
	}

	public static String get() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Session> s : SESSIONS.entrySet()) {
			sb.append(String.join(":", s.getKey(), s.getValue().toString()));
		}
		return sb.toString();
	}
}
