package org.apache.coyote.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.coyote.Cookie;
import org.apache.coyote.MimeType;

public class ResponseHeader {

	private static final Set<HeaderType> COMMON_HEADERS = Set.of(
		HeaderType.CONTENT_TYPE,
		HeaderType.CONTENT_LENGTH,
		HeaderType.SET_COOKIE
	);

	private final Map<HeaderType, String> headers = new HashMap<>();
	private final List<Cookie> cookies = new ArrayList<>();

	public ResponseHeader(final Map<HeaderType, String> headers) {
		this.headers.putAll(headers);
	}

	public void addContentLength(final MimeType mimeType) {
		headers.put(HeaderType.CONTENT_LENGTH, mimeType.getValue());
	}

	public void addContentLength(final String responseBody) {
		headers.put(HeaderType.CONTENT_LENGTH, Integer.toString(responseBody.getBytes().length));
	}

	public void addCookie(final Cookie cookie) {
		cookies.add(cookie);
	}

	public void add(HeaderType key, String value) {
		if (COMMON_HEADERS.contains(key)) {
			return;
		}
		headers.put(key, value);
	}

	public Map<HeaderType, String> getHeaders() {
		return headers;
	}

	public List<Cookie> getCookies() {
		return cookies;
	}
}
