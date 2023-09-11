package org.apache.coyote.request;

import java.util.HashMap;
import java.util.Map;

public class RequestBody {

	private final Map<String, String> fields = new HashMap<>();

	private RequestBody() {
	}

	public RequestBody(final Map<String, String> fields) {
		this.fields.putAll(fields);
	}

	public static RequestBody empty() {
		return new RequestBody();
	}

	public String findField(final String key) {
		return fields.get(key);
	}
}
