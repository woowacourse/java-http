package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.techcourse.model.User;

public final class SessionManager {
	private static final Map<Session, User> sessions = new HashMap<>();

	public static Session createSession(User user) {
		Session session = new Session();
		sessions.put(session, user);
		return session;
	}

	public static Optional<User> findUserBySession(Session session) {
		return Optional.ofNullable(sessions.get(session));
	}
}
