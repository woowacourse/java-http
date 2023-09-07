package org.apache.coyote;

public class Cookie {

	private static final String SESSION_ID_KEY = "JSESSIONID";

	private final String key;
	private final String value;

	public Cookie(final String key, final String value) {
		this.key = key;
		this.value = value;
	}

	public static Cookie session(final String sessionId) {
		return new Cookie(SESSION_ID_KEY, sessionId);
	}

	public boolean isSession() {
		return SESSION_ID_KEY.equals(key);
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
