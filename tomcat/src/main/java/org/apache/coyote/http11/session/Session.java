package org.apache.coyote.http11.session;

public class Session {

	private final String id;

	public Session(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
