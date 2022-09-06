package org.apache.coyote.http11.http;

import static org.apache.coyote.http11.http.Session.*;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

	private static final Map<String, Session> SESSIONS = new HashMap<>();

	private SessionManager() {}

	public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
	}

	public static Session findSession(Cookie cookie) {
		return SESSIONS.getOrDefault(cookie.getValue(JSESSIONID), createNewSession());
	}

	private static Session createNewSession() {
		Session session = new Session();
		SESSIONS.put(session.getId(), session);
		return session;
	}
}
