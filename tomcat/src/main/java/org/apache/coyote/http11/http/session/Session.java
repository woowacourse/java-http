package org.apache.coyote.http11.http.session;

import com.techcourse.model.User;

public class Session {

	private final String id;
	private final User user;

	public Session(String id, User user) {
		this.id = id;
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public User getUser() {
		return user;
	}
}
