package org.apache.coyote.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.techcourse.model.User;

public final class SessionManager {
	private static final Map<String, User> sessions = new HashMap<>();

	public static void createSession(User user) {
		String sessionId = "sessionId";
		sessions.put(sessionId, user);
	}

	public static Optional<User> findUserBySession(String sessionId) {
		User user = sessions.getOrDefault(sessionId, null);
		if(user == null) {
			return Optional.empty();
		}
		return Optional.of(user);
	}
}
