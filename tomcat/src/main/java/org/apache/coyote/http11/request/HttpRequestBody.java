package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestBody {

	private static final String DELIMITER = "&";
	private static final String KEY_VALUE_SEPARATOR = "=";

	private final Map<String, String> fields;

	private HttpRequestBody() {
		this.fields = Collections.emptyMap();
	}

	private HttpRequestBody(final Map<String, String> fields) {
		this.fields = fields;
	}

	public static HttpRequestBody empty() {
		return new HttpRequestBody();
	}

	public static HttpRequestBody from(String body) {
		return Arrays.stream(body.split(DELIMITER))
			.map(field -> field.split(KEY_VALUE_SEPARATOR))
			.collect(Collectors.collectingAndThen(
				Collectors.toMap(field -> field[0], field -> field[1]),
				HttpRequestBody::new
			));
	}

	public String findField(final String key) {
		return fields.get(key);
	}
}
