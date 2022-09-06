package org.apache.coyote.http11.http.session;

import static org.apache.coyote.http11.http.session.Session.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.http.header.Cookie;

public class SessionManager {

	private static final Map<String, Session> SESSIONS = new HashMap<>();

	private SessionManager() {
	}

	public static Session add(final Session session) {
		SESSIONS.put(session.getId(), session);
		return session;
	}

	public static Optional<Session> findSession(Cookie cookie) {
		return Optional.ofNullable(SESSIONS.get(cookie.getValue(JSESSIONID)));
	}
}
