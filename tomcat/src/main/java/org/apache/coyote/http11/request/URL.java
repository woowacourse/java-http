package org.apache.coyote.http11.request;

import static nextstep.jwp.util.Parser.findParam;
import static nextstep.jwp.util.Parser.findUrl;

import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.exception.ExceptionType;
import nextstep.jwp.exception.InvalidHttpRequestException;

public class URL {

	private final String path;
	private Map<String, String> params;

	private URL(String value, Map<String, String> params) {
		validate(value);
		this.path = value;
		this.params = new HashMap<>(params);
	}

	public static URL from(String url) {
		return new URL(findUrl(url), findParam(url));
	}

	private void validate(String path) {
		if (path.isEmpty()) {
			throw new InvalidHttpRequestException(ExceptionType.INVALID_URL_EXCEPTION);
		}
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getParams() {
		return params;
	}
}
