package org.apache.coyote.http11.request;

import static nextstep.jwp.exception.ExceptionType.INVALID_REQUEST_BODY_EXCEPTION;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.exception.InvalidHttpRequestException;
import nextstep.jwp.util.IOUtils;
import nextstep.jwp.util.Parser;

public class RequestBody {

	private final Map<String, String> requestBody;

	private RequestBody(Map<String, String> requestBody) {
		this.requestBody = new HashMap<>(requestBody);
	}

	public static RequestBody from(Map<String, String> requestBody) {
		return new RequestBody(requestBody);
	}

	public static RequestBody of(final BufferedReader reader, final int contentLength) {
		try {
			final String body = IOUtils.readData(reader, contentLength);
			final Map<String, String> param = Parser.parseQueryString(body);
			return new RequestBody(param);
		} catch (IOException e) {
			throw new InvalidHttpRequestException(INVALID_REQUEST_BODY_EXCEPTION);
		}
	}

	public Map<String, String> getRequestBody() {
		return requestBody;
	}
}
