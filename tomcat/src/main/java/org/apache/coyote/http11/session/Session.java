package org.apache.coyote.http11.session;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class Session {
	public static final String SESSION_HEADER_KEY = "JSESSIONID";
	private final String id;

	public Session() {
		this.id = UUID.randomUUID().toString();
	}

	public Session(String cookies) {
		this.id = Arrays.asList(cookies.split("; "))
			.stream()
			.filter(cookie -> cookie.startsWith(SESSION_HEADER_KEY))
			.findAny()
			.orElseGet(null);
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Session session = (Session)o;
		return Objects.equals(id, session.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
