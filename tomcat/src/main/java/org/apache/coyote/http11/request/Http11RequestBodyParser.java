package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.*;

import java.util.Arrays;

import org.apache.coyote.request.RequestBody;

public class Http11RequestBodyParser {

	private static final String DELIMITER = "&";
	private static final String KEY_VALUE_SEPARATOR = "=";

	private Http11RequestBodyParser() {
	}

	public static RequestBody parse(final String body) {
		if (body == null || body.isBlank()) {
			return RequestBody.empty();
		}
		return Arrays.stream(body.split(DELIMITER))
			.map(field -> field.split(KEY_VALUE_SEPARATOR, 2))
			.filter(field -> field.length == 2)
			.collect(collectingAndThen(
				toMap(parts -> parts[0], field -> field[1]),
				RequestBody::new
			));
	}
}
