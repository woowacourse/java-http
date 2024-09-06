package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.techcourse.model.User;

public final class SessionManager {
	private static final Map<String, User> sessions = new HashMap<>();

	public static void createSession(User user) {
		String sessionId = UUID.randomUUID().toString();
		sessions.put(sessionId, user);
	}

	public static Optional<User> findUserBySession(String sessionId) {
		return Optional.ofNullable(sessions.get(sessionId));
	}
}
