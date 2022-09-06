package org.apache.coyote.http11.http.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Session {

	public static final String JSESSIONID = "JSESSIONID";

	private final String id;
	private final Map<String, Object> values = new HashMap<>();

	public Session() {
		this.id = UUID.randomUUID().toString();
	}

	public Optional<Object> getAttribute(final String name) {
		return Optional.ofNullable(values.get(name));
	}

	public void setAttribute(final String name, final Object value) {
		values.put(name, value);
	}

	public String getId() {
		return id;
	}
}
