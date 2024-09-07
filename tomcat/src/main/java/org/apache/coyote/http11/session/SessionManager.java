package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.techcourse.model.User;

public final class SessionManager {
	private static final Map<String, User> sessions = new HashMap<>();

	public static String createSession(User user) {
		String sessionId = UUID.randomUUID().toString();
		sessions.put(sessionId, user);
		return sessionId;
	}

	public static Optional<User> findUserBySession(String sessionId) {
		return Optional.ofNullable(sessions.get(sessionId));
	}
}
