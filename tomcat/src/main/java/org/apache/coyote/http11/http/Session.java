package org.apache.coyote.http11.http;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class Session {

	public static final String JSESSIONID = "JSESSIONID";

	private final String id;
	private final Map<String, Object> values = new HashMap<>();

	public Session() {
		this.id = UUID.randomUUID().toString();
	}

	public Object getAttribute(final String name) {
		return Optional.ofNullable(values.get(name))
			.orElseThrow(() -> new NoSuchElementException("해당 세션 값이 없습니다."));
	}

	public void setAttribute(final String name, final Object value) {
		values.put(name, value);
	}

	public String getId() {
		return id;
	}
}
