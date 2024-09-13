package org.apache.coyote.http11.session;

import static org.apache.coyote.http11.common.HttpDelimiter.*;

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
		String session = getSession(cookies);
		if (session != null) {
			this.id = session.substring(SESSION_HEADER_KEY.length() + 1);
		} else {
			this.id = null;
		}
	}

	private static String getSession(String cookies) {
		String session = Arrays.asList(cookies.split(SESSION_DELIMITER.getValue()))
			.stream()
			.filter(cookie -> cookie.startsWith(SESSION_HEADER_KEY))
			.findAny()
			.orElseGet(null);
		return session;
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
