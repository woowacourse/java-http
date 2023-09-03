package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBody {

	private static final String DELIMITER = "&";
	private static final String KEY_VALUE_SEPARATOR = "=";

	private final Map<String, String> fields = new HashMap<>();

	private RequestBody() {
	}

	private RequestBody(final Map<String, String> fields) {
		this.fields.putAll(fields);
	}

	public static RequestBody empty() {
		return new RequestBody();
	}

	public static RequestBody from(String body) {
		return Arrays.stream(body.split(DELIMITER))
			.map(field -> field.split(KEY_VALUE_SEPARATOR))
			.collect(Collectors.collectingAndThen(
				Collectors.toMap(parts -> parts[0], field -> field[1]),
				RequestBody::new
			));
	}

	public String findField(final String key) {
		return fields.get(key);
	}
}
