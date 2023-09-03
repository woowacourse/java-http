package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;

public class Session {

	private final String id;
	private final Map<String, Object> attributes = new HashMap<>();

	public Session(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttribute(final String name, final Object value) {
		attributes.put(name, value);
	}

	public void removeAttribute(final String name) {
		attributes.remove(name);
	}

	public void invalidate() {
		attributes.clear();
	}
}
